package io.github.xfacthd.contex.client.data;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.utils.Utils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.resources.Identifier;
import io.github.xfacthd.contex.api.type.TextureType;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public record TextureEntry(Identifier baseTexture, Reference2ObjectMap<SpriteType, Identifier> textures)
{
    public static final Codec<TextureEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("main_texture").forGetter(TextureEntry::baseTexture),
            Utils.ref2ObjMapCodec(SpriteType.CODEC, Identifier.CODEC).optionalFieldOf("ct_textures", Reference2ObjectMaps.emptyMap()).forGetter(TextureEntry::textures)
    ).apply(inst, TextureEntry::new));

    public Identifier get(@Nullable SpriteType type)
    {
        return textures.getOrDefault(type, baseTexture);
    }

    @Nullable
    Set<SpriteType> validateSpriteTypes(TextureType type)
    {
        if (!type.getSpriteTypes().containsAll(textures.keySet()))
        {
            return Sets.difference(textures.keySet(), type.getSpriteTypes());
        }
        return null;
    }

    void resolve(TextureType texType)
    {
        for (SpriteType spriteType : texType.getSpriteTypes())
        {
            textures.computeIfAbsent(spriteType, (SpriteType type) -> baseTexture.withSuffix("_" + type.suffix()));
        }
    }
}
