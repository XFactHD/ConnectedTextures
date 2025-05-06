package xfacthd.contex.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xfacthd.contex.api.texture.Border;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record ConTexSpriteSupplier(
        ResourceLocation srcLoc,
        ResourceLocation outLoc,
        Resource imgResource,
        LazyLoadedImage image,
        Border border
) implements SpriteSource.SpriteSupplier
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public ConTexSpriteSupplier(ResourceLocation srcLoc, ResourceLocation outLoc, Resource imgResource, Border border)
    {
        this(srcLoc, outLoc, imgResource, new LazyLoadedImage(srcLoc, imgResource, 1), border);
    }

    @Override
    @Nullable
    public SpriteContents apply(SpriteResourceLoader loader)
    {
        try
        {
            return createTexture(srcLoc, outLoc, image.get(), imgResource.metadata(), border);
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to generate CTM texture from texture '{}'", srcLoc, e);
            return null;
        }
        finally
        {
            image.release();
        }
    }

    @Nullable
    public static SpriteContents createTexture(
            ResourceLocation srcLoc,
            ResourceLocation outLoc,
            NativeImage srcImage,
            ResourceMetadata metadata,
            Border border
    )
    {
        AnimationMetadataSection animMeta = metadata.getSection(AnimationMetadataSection.TYPE).orElse(null);
        FrameSize srcSize = computeFrameSize(srcLoc, animMeta, srcImage);
        if (srcSize == null || !border.canApplyTo(srcSize))
        {
            return null;
        }

        NativeImage destImage = new NativeImage(srcImage.format(), srcImage.getWidth() * 2, srcImage.getHeight() * 2, false);
        FrameSize destSize = computeFrameSize(srcLoc, animMeta, destImage);
        if (destSize == null)
        {
            destImage.close();
            return null;
        }

        List<FrameInfo> frames = collectFrames(srcImage, srcSize, animMeta);
        for (FrameInfo frame : frames)
        {
            OutputFrame.of(srcImage, destImage, border, frame, srcSize, destSize).build();
        }

        return new SpriteContents(outLoc, destSize, destImage, metadata);
    }

    @Nullable
    private static FrameSize computeFrameSize(ResourceLocation srcLoc, @Nullable AnimationMetadataSection animMeta, NativeImage image)
    {
        if (animMeta == null)
        {
            return new FrameSize(image.getWidth(), image.getHeight());
        }

        FrameSize size = animMeta.calculateFrameSize(image.getWidth(), image.getHeight());
        if (!Mth.isMultipleOf(image.getWidth(), size.width()) || !Mth.isMultipleOf(image.getHeight(), size.height()))
        {
            LOGGER.error("Image '{}' size {}x{} is not multiple of frame size {}x{}", srcLoc, image.getWidth(), image.getHeight(), size.width(), size.height());
            return null;
        }
        return size;
    }

    private static List<FrameInfo> collectFrames(NativeImage image, FrameSize size, @Nullable AnimationMetadataSection anim)
    {
        if (anim == null)
        {
            return List.of(FrameInfo.ZERO);
        }

        List<FrameInfo> frames = new ArrayList<>();
        int rowCount = image.getWidth() / size.width();
        // Collect explicitly specified frames
        if (anim.frames().isPresent())
        {
            for (AnimationFrame frame : anim.frames().get())
            {
                frames.add(FrameInfo.of(frame.index(), rowCount));
            }
        }
        // Collect implicit frames if no explicit ones are specified in the animation
        if (frames.isEmpty())
        {
            int frameCount = rowCount * (image.getHeight() / size.height());
            for (int idx = 0; idx < frameCount; idx++)
            {
                frames.add(FrameInfo.of(idx, rowCount));
            }
        }
        return frames;
    }

    private record OutputFrame(
            NativeImage srcImage,
            NativeImage destImage,
            int srcWidth,
            int srcHeight,
            int srcX,
            int srcY,
            int destX,
            int destY,
            int left,
            int right,
            int bottom,
            int top,
            int vertWidth,
            int horHeight,
            boolean mirrorPar,
            boolean mirrorPerp
    )
    {
        static OutputFrame of(NativeImage srcImage, NativeImage destImage, Border border, FrameInfo frame, FrameSize srcSize, FrameSize destSize)
        {
            int srcWidth = srcSize.width();
            int srcHeight = srcSize.height();
            int srcX = srcWidth * frame.xIdx;
            int srcY = srcHeight * frame.yIdx;
            int destX = destSize.width() * frame.xIdx;
            int destY = destSize.height() * frame.yIdx;
            int left = border.left();
            int right = border.right();
            int bottom = border.bottom();
            int top = border.top();
            int vertWidth = srcWidth - left - right;
            int horHeight = srcHeight - bottom - top;
            boolean mirrorPar = border.mirrorParallel();
            boolean mirrorPerp = border.mirrorPerpendicular();
            return new OutputFrame(srcImage, destImage, srcWidth, srcHeight, srcX, srcY, destX, destY, left, right, bottom, top, vertWidth, horHeight, mirrorPar, mirrorPerp);
        }

        void build()
        {
            // Fully connected (top left)
            srcImage.copyRect(destImage, srcX, srcY, destX, destY, srcWidth, srcHeight, false, false);
            // Top edge
            copyRect(0, 0, left, top, 0, -top, vertWidth, top, mirrorPerp, mirrorPar);
            // Bottom edge
            copyRect(0, 0, left, srcHeight - (bottom * 2), 0, bottom, vertWidth, bottom, mirrorPerp, mirrorPar);
            // Left edge
            copyRect(0, 0, left, top, -left, 0, left, horHeight, mirrorPar, mirrorPerp);
            // Right edge
            copyRect(0, 0, srcWidth - (right * 2), top, right, 0, right, horHeight, mirrorPar, mirrorPerp);
            // Top-left corner
            copyRect(0, 0, left, top, -left, -top, left, top, mirrorPar, mirrorPar);
            // Top-right corner
            copyRect(0, 0, srcWidth - (right * 2), top, right, -top, right, top, mirrorPar, mirrorPar);
            // Bottom-left corner
            copyRect(0, 0, left, srcHeight - (bottom * 2), -left, bottom, left, bottom, mirrorPar, mirrorPar);
            // Bottom-right corner
            copyRect(0, 0, srcWidth - (right * 2), srcHeight - (bottom * 2), right, bottom, right, bottom, mirrorPar, mirrorPar);

            // Vertically connected (top right)
            srcImage.copyRect(destImage, srcX, srcY, destX + srcWidth, destY, srcWidth, srcHeight, false, false);
            // Top edge
            copyRect(1, 0, left, top, 0, -top, vertWidth, top, mirrorPerp, mirrorPar);
            // Bottom edge
            copyRect(1, 0, left, srcHeight - (bottom * 2), 0, bottom, vertWidth, bottom, mirrorPerp, mirrorPar);

            // Horizontally connected (bottom left)
            srcImage.copyRect(destImage, srcX, srcY, destX, destY + srcHeight, srcWidth, srcHeight, false, false);
            // Left edge
            copyRect(0, 1, left, top, -left, 0, left, horHeight, mirrorPar, mirrorPerp);
            // Right edge
            copyRect(0, 1, srcWidth - (right * 2), top, right, 0, right, horHeight, mirrorPar, mirrorPerp);

            // Horizontally and vertically connected (bottom right)
            srcImage.copyRect(destImage, srcX, srcY, destX + srcWidth, destY + srcHeight, srcWidth, srcHeight, false, false);
            // Top edge
            copyRect(1, 1, left, top, 0, -top, vertWidth, top, mirrorPerp, mirrorPar);
            // Bottom edge
            copyRect(1, 1, left, srcHeight - (bottom * 2), 0, bottom, vertWidth, bottom, mirrorPerp, mirrorPar);
            // Left edge
            copyRect(1, 1, left, top, -left, 0, left, horHeight, mirrorPar, mirrorPerp);
            // Right edge
            copyRect(1, 1, srcWidth - (right * 2), top, right, 0, right, horHeight, mirrorPar, mirrorPerp);
        }

        void copyRect(int quadrantX, int quadrantY, int srcX, int srcY, int offX, int offY, int width, int height, boolean mirrorX, boolean mirrorY)
        {
            int destX = this.destX + (quadrantX * srcWidth) + srcX + offX;
            int destY = this.destY + (quadrantY * srcHeight) + srcY + offY;
            srcImage.copyRect(destImage, this.srcX + srcX, this.srcY + srcY, destX, destY, width, height, mirrorX, mirrorY);
        }
    }

    @Override
    public void discard()
    {
        image.release();
    }

    private record FrameInfo(int idx, int xIdx, int yIdx)
    {
        private static final FrameInfo ZERO = new FrameInfo(0, 0, 0);

        private static FrameInfo of(int idx, int rowCount)
        {
            int frameX = idx % rowCount;
            int frameY = idx / rowCount;
            return new FrameInfo(idx, frameX, frameY);
        }
    }
}
