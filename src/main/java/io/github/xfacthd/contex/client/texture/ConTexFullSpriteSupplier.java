package io.github.xfacthd.contex.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import io.github.xfacthd.contex.api.texture.Border;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.client.strategy.FullTextureStrategy;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public record ConTexFullSpriteSupplier(
        Identifier srcLoc,
        Identifier outLoc,
        SpriteType type,
        Resource imgResource,
        LazyLoadedImage image,
        Border border,
        CompactImageCache imageCache,
        Set<MetadataSectionType<?>> additionalMetadata
) implements SpriteSource.DiscardableLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ConTexFullSpriteSupplier(Identifier srcLoc, Identifier outLoc, SpriteType outType, Resource imgResource, Border border, CompactImageCache imageCache, Set<MetadataSectionType<?>> additionalMetadata) {
        this(srcLoc, outLoc, outType, imgResource, new LazyLoadedImage(srcLoc, imgResource, 1), border, imageCache, additionalMetadata);
    }

    @Override
    public @Nullable SpriteContents get(SpriteResourceLoader loader) {
        try {
            NativeImage srcImg = image.get();
            ResourceMetadata metadata = imgResource.metadata();

            FullTextureStrategy.SpriteTypeTuple partTypes = FullTextureStrategy.getPartTypes(type);
            ConTexCompactSpriteSupplier.Image topLeft = imageCache.get(partTypes.topLeft(), srcLoc, srcImg, metadata, border);
            ConTexCompactSpriteSupplier.Image topRight = imageCache.get(partTypes.topRight(), srcLoc, srcImg, metadata, border);
            ConTexCompactSpriteSupplier.Image bottomLeft = imageCache.get(partTypes.bottomLeft(), srcLoc, srcImg, metadata, border);
            ConTexCompactSpriteSupplier.Image bottomRight = imageCache.get(partTypes.bottomRight(), srcLoc, srcImg, metadata, border);
            if (topLeft == null || topRight == null || bottomLeft == null || bottomRight == null) {
                return null;
            }

            FrameSize size = topLeft.size();
            int halfWidth = size.width() / 2;
            int halfHeight = size.height() / 2;
            NativeImage destImage = new NativeImage(srcImg.format(), srcImg.getWidth(), srcImg.getHeight(), false);
            for (ConTexCompactSpriteSupplier.FrameInfo frame : topLeft.frames()) {
                int x = size.width() * frame.xIdx();
                int y = size.height() * frame.yIdx();
                topLeft.image().copyRect(destImage, x, y, x, y, halfWidth, halfHeight, false, false);
                topRight.image().copyRect(destImage, x + halfWidth, y, x + halfWidth, y, halfWidth, halfHeight, false, false);
                bottomLeft.image().copyRect(destImage, x, y + halfHeight, x, y + halfHeight, halfWidth, halfHeight, false, false);
                bottomRight.image().copyRect(destImage, x + halfWidth, y + halfHeight, x + halfWidth, y + halfHeight, halfWidth, halfHeight, false, false);
            }

            Optional<AnimationMetadataSection> animMeta = metadata.getSection(AnimationMetadataSection.TYPE);
            List<MetadataSectionType.WithValue<?>> typedMetadata = metadata.getTypedSections(additionalMetadata);
            Optional<TextureMetadataSection> texMeta = metadata.getSection(TextureMetadataSection.TYPE);
            return new SpriteContents(outLoc, size, destImage, animMeta, typedMetadata, texMeta);
        } catch (Throwable e) {
            LOGGER.error("Failed to generate CTM texture from texture '{}' for sprite type '{}'", srcLoc, type, e);
            return null;
        } finally {
            image.release();
            imageCache.release();
        }
    }

    @Override
    public void discard() {
        image.release();
        imageCache.release();
    }

    public static final class CompactImageCache {
        private final Map<SpriteType, ConTexCompactSpriteSupplier.Image> images = new ConcurrentHashMap<>();
        private final AtomicInteger refCount;

        public CompactImageCache(int refCount) {
            this.refCount = new AtomicInteger(refCount);
        }

        ConTexCompactSpriteSupplier.@Nullable Image get(SpriteType type, Identifier srcLoc, NativeImage srcImage, ResourceMetadata metadata, Border border) {
            return images.computeIfAbsent(type, key -> ConTexCompactSpriteSupplier.createImage(srcLoc, key, srcImage, metadata, border));
        }

        void release() {
            int references = refCount.decrementAndGet();
            if (references <= 0) {
                images.values().removeIf(img -> {
                    img.image().close();
                    return true;
                });
            }
        }
    }
}
