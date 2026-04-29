package io.github.xfacthd.contex.api.texture;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.client.strategy.FullTextureStrategy;
import io.github.xfacthd.contex.client.texture.ConTexCompactSpriteSupplier;
import io.github.xfacthd.contex.client.texture.ConTexFullSpriteSupplier;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Optional;
import java.util.Set;

public record ConTexSpriteSource(Identifier texture, Border border, boolean compact) implements SpriteSource {
    public static final MapCodec<ConTexSpriteSource> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("texture").forGetter(ConTexSpriteSource::texture),
            Border.CODEC.fieldOf("border").forGetter(ConTexSpriteSource::border),
            Codec.BOOL.optionalFieldOf("compact", true).forGetter(ConTexSpriteSource::compact)
    ).apply(inst, ConTexSpriteSource::new));

    public ConTexSpriteSource(Identifier texture, Border border) {
        this(texture, border, true);
    }

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        run(resourceManager, output, Set.of());
    }

    @Override
    public void run(ResourceManager resourceManager, Output output, Set<MetadataSectionType<?>> additionalMetadata) {
        Identifier texLoc = TEXTURE_ID_CONVERTER.idToFile(texture);
        Optional<Resource> resource = resourceManager.getResource(texLoc);
        if (resource.isEmpty()) {
            LogUtils.getLogger().warn("Missing sprite: {}", texture);
            return;
        }

        if (compact) {
            for (SpriteType type : SpriteType.BASE_TYPES) {
                if (type == SpriteType.NONE) {
                    continue;
                }

                Identifier outLoc = texture.withSuffix("_" + type.suffix());
                output.add(outLoc, new ConTexCompactSpriteSupplier(texture, outLoc, type, resource.get(), border, additionalMetadata));
            }
        } else {
            ConTexFullSpriteSupplier.CompactImageCache imageCache = new ConTexFullSpriteSupplier.CompactImageCache(FullTextureStrategy.TYPES.size() - 1);
            for (SpriteType type : FullTextureStrategy.TYPES) {
                if (type == SpriteType.NONE) {
                    continue;
                }

                Identifier outLoc = texture.withSuffix("_" + type.suffix());
                output.add(outLoc, new ConTexFullSpriteSupplier(texture, outLoc, type, resource.get(), border, imageCache, additionalMetadata));
            }
        }
    }

    @Override
    public MapCodec<? extends SpriteSource> codec() {
        return CODEC;
    }
}
