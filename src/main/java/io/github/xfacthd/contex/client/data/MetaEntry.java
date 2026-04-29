package io.github.xfacthd.contex.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.client.predicate.SameBlockPredicate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record MetaEntry(TextureType type, ConnectionPredicate predicate, OcclusionMode occlusionMode, Optional<StatePredicate> statePredicate, TextureEntry[] textures) {
    public static final Codec<MetaEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            MetadataRegistry.TYPE_CODEC.fieldOf("type").forGetter(MetaEntry::type),
            MetadataRegistry.PREDICATE_CODEC.optionalFieldOf("predicate", SameBlockPredicate.INSTANCE).forGetter(MetaEntry::predicate),
            OcclusionMode.CODEC.optionalFieldOf("occlusion_mode", OcclusionMode.SELF).forGetter(MetaEntry::occlusionMode),
            StatePredicate.CODEC.optionalFieldOf("state_predicate").forGetter(MetaEntry::statePredicate),
            TextureEntry.CODEC.listOf().fieldOf("textures").forGetter(MetaEntry::textureList)
    ).apply(inst, MetaEntry::new));

    private MetaEntry(TextureType type, ConnectionPredicate predicate, OcclusionMode occlusionMode, Optional<StatePredicate> statePredicate, List<TextureEntry> textures) {
        this(type, predicate, occlusionMode, statePredicate, textures.toArray(TextureEntry[]::new));
    }

    private List<TextureEntry> textureList() {
        return List.of(textures);
    }

    public Baked bake(MaterialBaker baker, TextureStrategy strategy) {
        TextureEntry.Baked[] bakedTextures = new TextureEntry.Baked[textures.length];
        Arrays.setAll(bakedTextures, i -> textures[i].bake(baker, type, strategy));
        return new Baked(type, predicate, occlusionMode, bakedTextures);
    }

    public static DataResult<List<MetaEntry>> validate(List<MetaEntry> metadata, TextureStrategy strategy) {
        record Key(Identifier texture, Optional<StatePredicate> statePredicate) { }

        Map<Key, MetaEntry> uniqueTextures = new HashMap<>();
        for (int metaIdx = 0; metaIdx < metadata.size(); metaIdx++) {
            MetaEntry entry = metadata.get(metaIdx);
            TextureEntry[] textures = entry.textures();
            for (int texIdx = 0; texIdx < textures.length; texIdx++) {
                TextureEntry texture = textures[texIdx];
                Set<SpriteType> invalidTypes = texture.validateSpriteTypes(entry.type, strategy);
                if (invalidTypes != null) {
                    int finalMetaIdx = metaIdx;
                    int finalTexIdx = texIdx;
                    return DataResult.error(() -> "Found CT unsupported texture keys in contex_meta[%d].textures[%d]: %s".formatted(
                            finalMetaIdx, finalTexIdx, invalidTypes
                    ));
                }

                Key key = new Key(texture.baseTexture(), entry.statePredicate);
                MetaEntry lastEntry = uniqueTextures.put(key, entry);
                if (lastEntry != null) {
                    int finalMetaIdx = metaIdx;
                    int finalTexIdx = texIdx;
                    return DataResult.error(() -> "Found duplicate texture '%s' in meta entry contex_meta[%d].textures[%d], previously found in meta entry contex_meta[%d] with identical state_predicate".formatted(
                            texture.baseTexture(), finalMetaIdx, finalTexIdx, metadata.indexOf(entry)
                    ));
                }
            }
        }
        return DataResult.success(metadata);
    }

    public record Baked(TextureType type, ConnectionPredicate predicate, OcclusionMode occlusionMode, TextureEntry.Baked[] textures) {
        public TextureEntry.Baked texture(int texIdx) {
            return textures[texIdx];
        }

        public int findTexture(TextureAtlasSprite sprite) {
            for (int i = 0; i < textures.length; i++) {
                TextureEntry.Baked texture = textures[i];
                if (texture.baseSprite() == sprite) {
                    return i;
                }
            }
            return -1;
        }
    }
}
