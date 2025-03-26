package xfacthd.contex.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.type.*;
import xfacthd.contex.client.predicate.SameBlockPredicate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MetaEntry(TextureType type, ConnectionPredicate predicate, OcclusionMode occlusionMode, TextureEntry[] textures)
{
    public static final Codec<MetaEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            MetadataRegistry.TYPE_CODEC.fieldOf("type").forGetter(MetaEntry::type),
            MetadataRegistry.PREDICATE_CODEC.optionalFieldOf("predicate", SameBlockPredicate.INSTANCE).forGetter(MetaEntry::predicate),
            OcclusionMode.CODEC.optionalFieldOf("occlusion_mode", OcclusionMode.SELF).forGetter(MetaEntry::occlusionMode),
            TextureEntry.CODEC.listOf().fieldOf("textures").forGetter(MetaEntry::textureList)
    ).apply(inst, MetaEntry::new));

    private MetaEntry(TextureType type, ConnectionPredicate predicate, OcclusionMode occlusionMode, List<TextureEntry> textures)
    {
        this(type, predicate, occlusionMode, textures.toArray(TextureEntry[]::new));
    }

    private List<TextureEntry> textureList()
    {
        return List.of(textures);
    }

    public TextureEntry texture(int texIdx)
    {
        return textures[texIdx];
    }

    public int findTexture(ResourceLocation tex)
    {
        for (int i = 0; i < textures.length; i++)
        {
            TextureEntry texture = textures[i];
            if (texture.baseTexture().equals(tex))
            {
                return i;
            }
        }
        return -1;
    }

    public static DataResult<List<MetaEntry>> validate(List<MetaEntry> metadata)
    {
        Map<ResourceLocation, MetaEntry> uniqueTextures = new HashMap<>();
        for (int metaIdx = 0; metaIdx < metadata.size(); metaIdx++)
        {
            MetaEntry entry = metadata.get(metaIdx);
            TextureEntry[] textures = entry.textures();
            for (int texIdx = 0; texIdx < textures.length; texIdx++)
            {
                TextureEntry texture = textures[texIdx];
                MetaEntry lastEntry = uniqueTextures.put(texture.baseTexture(), entry);
                if (lastEntry != null)
                {
                    int finalMetaIdx = metaIdx;
                    int finalTexIdx = texIdx;
                    return DataResult.error(() -> "Found duplicate texture '%s' in meta entry contex_meta[%d].textures[%d], previously found in meta entry contex_meta[%d]".formatted(
                            texture.baseTexture(), finalMetaIdx, finalTexIdx, metadata.indexOf(entry)
                    ));
                }
            }
        }
        return DataResult.success(metadata);
    }
}
