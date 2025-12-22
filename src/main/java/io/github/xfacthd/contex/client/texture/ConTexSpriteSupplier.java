package io.github.xfacthd.contex.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import io.github.xfacthd.contex.api.texture.Border;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record ConTexSpriteSupplier(
        Identifier srcLoc,
        Identifier outLoc,
        Resource imgResource,
        LazyLoadedImage image,
        Border border,
        Set<MetadataSectionType<?>> additionalMetadata
) implements SpriteSource.DiscardableLoader
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public ConTexSpriteSupplier(Identifier srcLoc, Identifier outLoc, Resource imgResource, Border border, Set<MetadataSectionType<?>> additionalMetadata)
    {
        this(srcLoc, outLoc, imgResource, new LazyLoadedImage(srcLoc, imgResource, 1), border, additionalMetadata);
    }

    @Override
    @Nullable
    public SpriteContents get(SpriteResourceLoader loader)
    {
        try
        {
            return createTexture(srcLoc, outLoc, image.get(), imgResource.metadata(), border, additionalMetadata);
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
            Identifier srcLoc,
            Identifier outLoc,
            NativeImage srcImage,
            ResourceMetadata metadata,
            Border border,
            Set<MetadataSectionType<?>> additionalMetadata
    )
    {
        Optional<AnimationMetadataSection> animMeta = metadata.getSection(AnimationMetadataSection.TYPE);
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

        List<MetadataSectionType.WithValue<?>> typedMetadata = metadata.getTypedSections(additionalMetadata);
        Optional<TextureMetadataSection> texMeta = metadata.getSection(TextureMetadataSection.TYPE);
        return new SpriteContents(outLoc, destSize, destImage, animMeta, typedMetadata, texMeta);
    }

    @Nullable
    private static FrameSize computeFrameSize(Identifier srcLoc, Optional<AnimationMetadataSection> animMeta, NativeImage image)
    {
        if (animMeta.isEmpty())
        {
            return new FrameSize(image.getWidth(), image.getHeight());
        }

        FrameSize size = animMeta.get().calculateFrameSize(image.getWidth(), image.getHeight());
        if (!Mth.isMultipleOf(image.getWidth(), size.width()) || !Mth.isMultipleOf(image.getHeight(), size.height()))
        {
            LOGGER.error("Image '{}' size {}x{} is not multiple of frame size {}x{}", srcLoc, image.getWidth(), image.getHeight(), size.width(), size.height());
            return null;
        }
        return size;
    }

    private static List<FrameInfo> collectFrames(NativeImage image, FrameSize size, Optional<AnimationMetadataSection> anim)
    {
        if (anim.isEmpty())
        {
            return List.of(FrameInfo.ZERO);
        }

        List<FrameInfo> frames = new ArrayList<>();
        int rowCount = image.getWidth() / size.width();
        // Collect explicitly specified frames
        Optional<List<AnimationFrame>> srcFrames = anim.get().frames();
        if (srcFrames.isPresent())
        {
            for (AnimationFrame frame : srcFrames.get())
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
            boolean mirrorPerp,
            boolean oppositeEdge,
            boolean synthCorners
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
            boolean oppositeEdge = border.copyFromOppositeEdge();
            boolean synthCorners = border.synthesizeInnerCorners();
            return new OutputFrame(srcImage, destImage, srcWidth, srcHeight, srcX, srcY, destX, destY, left, right, bottom, top, vertWidth, horHeight, mirrorPar, mirrorPerp, oppositeEdge, synthCorners);
        }

        void build()
        {
            int srcXLeft = oppositeEdge ? (srcWidth - (right * 2)) : left;
            int srcXRight = oppositeEdge ? left : (srcWidth - (right * 2));
            int srcYTop = oppositeEdge ? (srcHeight - (bottom * 2)) : top;
            int srcYBottom = oppositeEdge ? top : (srcHeight - (bottom * 2));

            int offXLeft = oppositeEdge ? -(srcWidth - (right * 2)) : -left;
            int offXRight = oppositeEdge ? (srcWidth - (right * 2)) : right;
            int offYTop = oppositeEdge ? -(srcHeight - (bottom * 2)) : -top;
            int offYBottom = oppositeEdge ? (srcHeight - (bottom * 2)) : bottom;

            // Fully connected (top left)
            srcImage.copyRect(destImage, srcX, srcY, destX, destY, srcWidth, srcHeight, false, false);
            // Top edge
            copyRect(0, 0, left, srcYTop, 0, offYTop, vertWidth, top, mirrorPerp, mirrorPar);
            // Bottom edge
            copyRect(0, 0, left, srcYBottom, 0, offYBottom, vertWidth, bottom, mirrorPerp, mirrorPar);
            // Left edge
            copyRect(0, 0, srcXLeft, top, offXLeft, 0, left, horHeight, mirrorPar, mirrorPerp);
            // Right edge
            copyRect(0, 0, srcXRight, top, offXRight, 0, right, horHeight, mirrorPar, mirrorPerp);
            // Top-left corner
            copyRect(0, 0, srcXLeft, srcYTop, offXLeft, offYTop, left, top, mirrorPar, mirrorPar);
            // Top-right corner
            copyRect(0, 0, srcXRight, srcYTop, offXRight, offYTop, right, top, mirrorPar, mirrorPar);
            // Bottom-left corner
            copyRect(0, 0, srcXLeft, srcYBottom, offXLeft, offYBottom, left, bottom, mirrorPar, mirrorPar);
            // Bottom-right corner
            copyRect(0, 0, srcXRight, srcYBottom, offXRight, offYBottom, right, bottom, mirrorPar, mirrorPar);

            int srcVertX = synthCorners ? 0 : left;
            int srcVertWidth = synthCorners ? srcWidth : vertWidth;

            // Vertically connected (top right)
            srcImage.copyRect(destImage, srcX, srcY, destX + srcWidth, destY, srcWidth, srcHeight, false, false);
            // Top edge
            copyRect(1, 0, srcVertX, srcYTop, 0, offYTop, srcVertWidth, top, mirrorPerp, mirrorPar);
            // Bottom edge
            copyRect(1, 0, srcVertX, srcYBottom, 0, offYBottom, srcVertWidth, bottom, mirrorPerp, mirrorPar);

            int srcHorY = synthCorners ? 0 : top;
            int srcHorHeight = synthCorners ? srcHeight : horHeight;

            // Horizontally connected (bottom left)
            srcImage.copyRect(destImage, srcX, srcY, destX, destY + srcHeight, srcWidth, srcHeight, false, false);
            // Left edge
            copyRect(0, 1, srcXLeft, srcHorY, offXLeft, 0, left, srcHorHeight, mirrorPar, mirrorPerp);
            // Right edge
            copyRect(0, 1, srcXRight, srcHorY, offXRight, 0, right, srcHorHeight, mirrorPar, mirrorPerp);

            // Horizontally and vertically connected (bottom right)
            srcImage.copyRect(destImage, srcX, srcY, destX + srcWidth, destY + srcHeight, srcWidth, srcHeight, false, false);
            // Top edge
            copyRect(1, 1, left, srcYTop, 0, offYTop, vertWidth, top, mirrorPerp, mirrorPar);
            // Bottom edge
            copyRect(1, 1, left, srcYBottom, 0, offYBottom, vertWidth, bottom, mirrorPerp, mirrorPar);
            // Left edge
            copyRect(1, 1, srcXLeft, top, offXLeft, 0, left, horHeight, mirrorPar, mirrorPerp);
            // Right edge
            copyRect(1, 1, srcXRight, top, offXRight, 0, right, horHeight, mirrorPar, mirrorPerp);
            if (synthCorners)
            {
                // Top-left corner
                buildInnerCorner(0, srcYTop, srcXRight, 0, 0, 0, left, top, false, false);
                // Top-right corner
                buildInnerCorner(srcWidth - right, srcYTop, srcXLeft, 0, srcWidth - right, 0, right, top, true, false);
                // Bottom-left corner
                buildInnerCorner(0, srcYBottom, srcXRight, srcHeight - bottom, 0, srcHeight - bottom, left, bottom, false, true);
                // Bottom-right corner
                buildInnerCorner(srcWidth - right, srcYBottom, srcXLeft, srcHeight - bottom, srcWidth - right, srcHeight - bottom, right, bottom, true, true);
            }
        }

        void copyRect(int quadrantX, int quadrantY, int srcX, int srcY, int offX, int offY, int width, int height, boolean mirrorX, boolean mirrorY)
        {
            int destX = this.destX + (quadrantX * srcWidth) + srcX + offX;
            int destY = this.destY + (quadrantY * srcHeight) + srcY + offY;
            srcImage.copyRect(destImage, this.srcX + srcX, this.srcY + srcY, destX, destY, width, height, mirrorX, mirrorY);
        }

        void buildInnerCorner(int srcXVert, int srcYVert, int srcXHor, int srcYHor, int destX, int destY, int width, int height, boolean invX, boolean invY)
        {
            destX += srcWidth;
            destY += srcHeight;

            for (int y = 0; y < height; y++)
            {
                int checkY = invY ? (width - y - 1) : y;
                for (int x = 0; x < width; x++)
                {
                    int checkX = invX ? (width - x - 1) : x;
                    if (checkX == checkY)
                    {
                        int colVert = srcImage.getPixel(srcXVert + x, srcYVert + y);
                        int colHor = srcImage.getPixel(srcXHor + x, srcYHor + y);
                        int colOut = ARGB.average(colVert, colHor);
                        destImage.setPixel(destX + x, destY + y, colOut);
                    }
                    else if (checkX > checkY)
                    {
                        int colOut = srcImage.getPixel(srcXVert + x, srcYVert + y);
                        destImage.setPixel(destX + x, destY + y, colOut);
                    }
                    else
                    {
                        int colOut = srcImage.getPixel(srcXHor + x, srcYHor + y);
                        destImage.setPixel(destX + x, destY + y, colOut);
                    }
                }
            }
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
