package io.github.xfacthd.contex.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import io.github.xfacthd.contex.api.texture.Border;
import io.github.xfacthd.contex.api.type.SpriteType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record ConTexCompactSpriteSupplier(
        Identifier srcLoc,
        Identifier outLoc,
        SpriteType type,
        Resource imgResource,
        LazyLoadedImage image,
        Border border,
        Set<MetadataSectionType<?>> additionalMetadata
) implements SpriteSource.DiscardableLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ConTexCompactSpriteSupplier(Identifier srcLoc, Identifier outLoc, SpriteType outType, Resource imgResource, Border border, Set<MetadataSectionType<?>> additionalMetadata) {
        this(srcLoc, outLoc, outType, imgResource, new LazyLoadedImage(srcLoc, imgResource, 1), border, additionalMetadata);
    }

    @Override
    public @Nullable SpriteContents get(SpriteResourceLoader loader) {
        try {
            return createTexture(srcLoc, outLoc, type, image.get(), imgResource.metadata(), border, additionalMetadata);
        } catch (Throwable e) {
            LOGGER.error("Failed to generate CTM texture from texture '{}' for sprite type '{}'", srcLoc, type, e);
            return null;
        } finally {
            image.release();
        }
    }

    public static @Nullable SpriteContents createTexture(
            Identifier srcLoc,
            Identifier outLoc,
            SpriteType type,
            NativeImage srcImage,
            ResourceMetadata metadata,
            Border border,
            Set<MetadataSectionType<?>> additionalMetadata
    ) {
        Image image = createImage(srcLoc, type, srcImage, metadata, border);
        if (image == null) {
            return null;
        }

        Optional<AnimationMetadataSection> animMeta = metadata.getSection(AnimationMetadataSection.TYPE);
        List<MetadataSectionType.WithValue<?>> typedMetadata = metadata.getTypedSections(additionalMetadata);
        Optional<TextureMetadataSection> texMeta = metadata.getSection(TextureMetadataSection.TYPE);
        return new SpriteContents(outLoc, image.size, image.image, animMeta, typedMetadata, texMeta);
    }

    static @Nullable Image createImage(
            Identifier srcLoc,
            SpriteType type,
            NativeImage srcImage,
            ResourceMetadata metadata,
            Border border
    ) {
        Optional<AnimationMetadataSection> animMeta = metadata.getSection(AnimationMetadataSection.TYPE);
        FrameSize srcSize = computeFrameSize(srcLoc, animMeta, srcImage);
        if (srcSize == null || !border.canApplyTo(srcSize)) {
            return null;
        }
        FrameSize destSize = computeFrameSize(srcLoc, animMeta, srcImage);
        if (destSize == null) {
            return null;
        }

        NativeImage destImage = new NativeImage(srcImage.format(), srcImage.getWidth(), srcImage.getHeight(), false);
        List<FrameInfo> frames = collectFrames(srcImage, srcSize, animMeta);
        if (type == SpriteType.NONE) {
            destImage.copyFrom(srcImage);
        } else {
            for (FrameInfo frame : frames) {
                OutputFrame.of(srcImage, destImage, type, border, frame, srcSize, destSize).build();
            }
        }
        return new Image(destImage, destSize, frames);
    }

    private static @Nullable FrameSize computeFrameSize(Identifier srcLoc, Optional<AnimationMetadataSection> animMeta, NativeImage image) {
        if (animMeta.isEmpty()) {
            return new FrameSize(image.getWidth(), image.getHeight());
        }

        FrameSize size = animMeta.get().calculateFrameSize(image.getWidth(), image.getHeight());
        if (!Mth.isMultipleOf(image.getWidth(), size.width()) || !Mth.isMultipleOf(image.getHeight(), size.height())) {
            LOGGER.error("Image '{}' size {}x{} is not multiple of frame size {}x{}", srcLoc, image.getWidth(), image.getHeight(), size.width(), size.height());
            return null;
        }
        return size;
    }

    private static List<FrameInfo> collectFrames(NativeImage image, FrameSize size, Optional<AnimationMetadataSection> anim) {
        if (anim.isEmpty()) {
            return List.of(FrameInfo.ZERO);
        }

        List<FrameInfo> frames = new ArrayList<>();
        int rowCount = image.getWidth() / size.width();
        // Collect explicitly specified frames
        Optional<List<AnimationFrame>> srcFrames = anim.get().frames();
        if (srcFrames.isPresent()) {
            for (AnimationFrame frame : srcFrames.get()) {
                frames.add(FrameInfo.of(frame.index(), rowCount));
            }
        }
        // Collect implicit frames if no explicit ones are specified in the animation
        if (frames.isEmpty()) {
            int frameCount = rowCount * (image.getHeight() / size.height());
            for (int idx = 0; idx < frameCount; idx++) {
                frames.add(FrameInfo.of(idx, rowCount));
            }
        }
        return frames;
    }

    record Image(NativeImage image, FrameSize size, List<FrameInfo> frames) { }

    private record OutputFrame(
            NativeImage srcImage,
            NativeImage destImage,
            SpriteType type,
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
    ) {
        static OutputFrame of(NativeImage srcImage, NativeImage destImage, SpriteType type, Border border, FrameInfo frame, FrameSize srcSize, FrameSize destSize) {
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
            return new OutputFrame(srcImage, destImage, type, srcWidth, srcHeight, srcX, srcY, destX, destY, left, right, bottom, top, vertWidth, horHeight, mirrorPar, mirrorPerp, oppositeEdge, synthCorners);
        }

        void build() {
            int srcXLeft = oppositeEdge ? (srcWidth - (right * 2)) : left;
            int srcXRight = oppositeEdge ? left : (srcWidth - (right * 2));
            int srcYTop = oppositeEdge ? (srcHeight - (bottom * 2)) : top;
            int srcYBottom = oppositeEdge ? top : (srcHeight - (bottom * 2));

            int offXLeft = oppositeEdge ? -(srcWidth - (right * 2)) : -left;
            int offXRight = oppositeEdge ? (srcWidth - (right * 2)) : right;
            int offYTop = oppositeEdge ? -(srcHeight - (bottom * 2)) : -top;
            int offYBottom = oppositeEdge ? (srcHeight - (bottom * 2)) : bottom;

            switch (type) {
                case SpriteType spriteType when spriteType == SpriteType.FULL -> {
                    // Fully connected (top left)
                    srcImage.copyRect(destImage, srcX, srcY, destX, destY, srcWidth, srcHeight, false, false);
                    // Top edge
                    copyRect(left, srcYTop, 0, offYTop, vertWidth, top, mirrorPerp, mirrorPar);
                    // Bottom edge
                    copyRect(left, srcYBottom, 0, offYBottom, vertWidth, bottom, mirrorPerp, mirrorPar);
                    // Left edge
                    copyRect(srcXLeft, top, offXLeft, 0, left, horHeight, mirrorPar, mirrorPerp);
                    // Right edge
                    copyRect(srcXRight, top, offXRight, 0, right, horHeight, mirrorPar, mirrorPerp);
                    // Top-left corner
                    copyRect(srcXLeft, srcYTop, offXLeft, offYTop, left, top, mirrorPar, mirrorPar);
                    // Top-right corner
                    copyRect(srcXRight, srcYTop, offXRight, offYTop, right, top, mirrorPar, mirrorPar);
                    // Bottom-left corner
                    copyRect(srcXLeft, srcYBottom, offXLeft, offYBottom, left, bottom, mirrorPar, mirrorPar);
                    // Bottom-right corner
                    copyRect(srcXRight, srcYBottom, offXRight, offYBottom, right, bottom, mirrorPar, mirrorPar);
                }
                case SpriteType spriteType when spriteType == SpriteType.VERTICAL -> {
                    int srcVertX = synthCorners ? 0 : left;
                    int srcVertWidth = synthCorners ? srcWidth : vertWidth;

                    // Vertically connected (top right)
                    srcImage.copyRect(destImage, srcX, srcY, destX, destY, srcWidth, srcHeight, false, false);
                    // Top edge
                    copyRect(srcVertX, srcYTop, 0, offYTop, srcVertWidth, top, mirrorPerp, mirrorPar);
                    // Bottom edge
                    copyRect(srcVertX, srcYBottom, 0, offYBottom, srcVertWidth, bottom, mirrorPerp, mirrorPar);
                }
                case SpriteType spriteType when spriteType == SpriteType.HORIZONTAL -> {
                    int srcHorY = synthCorners ? 0 : top;
                    int srcHorHeight = synthCorners ? srcHeight : horHeight;

                    // Horizontally connected (bottom left)
                    srcImage.copyRect(destImage, srcX, srcY, destX, destY, srcWidth, srcHeight, false, false);
                    // Left edge
                    copyRect(srcXLeft, srcHorY, offXLeft, 0, left, srcHorHeight, mirrorPar, mirrorPerp);
                    // Right edge
                    copyRect(srcXRight, srcHorY, offXRight, 0, right, srcHorHeight, mirrorPar, mirrorPerp);
                }
                case SpriteType spriteType when spriteType == SpriteType.CROSS -> {
                    // Horizontally and vertically connected (bottom right)
                    srcImage.copyRect(destImage, srcX, srcY, destX, destY, srcWidth, srcHeight, false, false);
                    // Top edge
                    copyRect(left, srcYTop, 0, offYTop, vertWidth, top, mirrorPerp, mirrorPar);
                    // Bottom edge
                    copyRect(left, srcYBottom, 0, offYBottom, vertWidth, bottom, mirrorPerp, mirrorPar);
                    // Left edge
                    copyRect(srcXLeft, top, offXLeft, 0, left, horHeight, mirrorPar, mirrorPerp);
                    // Right edge
                    copyRect(srcXRight, top, offXRight, 0, right, horHeight, mirrorPar, mirrorPerp);
                    if (synthCorners) {
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
                default -> throw new IllegalArgumentException("Unsupported SpriteType: " + type);
            }
        }

        void copyRect(int srcX, int srcY, int offX, int offY, int width, int height, boolean mirrorX, boolean mirrorY) {
            int destX = this.destX + srcX + offX;
            int destY = this.destY + srcY + offY;
            srcImage.copyRect(destImage, this.srcX + srcX, this.srcY + srcY, destX, destY, width, height, mirrorX, mirrorY);
        }

        void buildInnerCorner(int srcXVert, int srcYVert, int srcXHor, int srcYHor, int destX, int destY, int width, int height, boolean invX, boolean invY) {
            for (int y = 0; y < height; y++) {
                int checkY = invY ? (width - y - 1) : y;
                for (int x = 0; x < width; x++) {
                    int checkX = invX ? (width - x - 1) : x;
                    if (checkX == checkY) {
                        int colVert = srcImage.getPixel(srcXVert + x, srcYVert + y);
                        int colHor = srcImage.getPixel(srcXHor + x, srcYHor + y);
                        int colOut = ARGB.average(colVert, colHor);
                        destImage.setPixel(destX + x, destY + y, colOut);
                    } else if (checkX > checkY) {
                        int colOut = srcImage.getPixel(srcXVert + x, srcYVert + y);
                        destImage.setPixel(destX + x, destY + y, colOut);
                    } else {
                        int colOut = srcImage.getPixel(srcXHor + x, srcYHor + y);
                        destImage.setPixel(destX + x, destY + y, colOut);
                    }
                }
            }
        }
    }

    @Override
    public void discard() {
        image.release();
    }

    record FrameInfo(int idx, int xIdx, int yIdx) {
        private static final FrameInfo ZERO = new FrameInfo(0, 0, 0);

        private static FrameInfo of(int idx, int rowCount) {
            int frameX = idx % rowCount;
            int frameY = idx / rowCount;
            return new FrameInfo(idx, frameX, frameY);
        }
    }
}
