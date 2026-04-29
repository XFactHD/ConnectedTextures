package io.github.xfacthd.contex.client.data;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.contex.api.model.SpriteLookup;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.api.utils.Utils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public record TextureEntry(Identifier baseTexture, Reference2ObjectMap<SpriteType, Identifier> textures)
{
    private static final Codec<TextureEntry> FULL_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("main_texture").forGetter(TextureEntry::baseTexture),
            Utils.ref2ObjMapCodec(SpriteType.CODEC, Identifier.CODEC)
                    .optionalFieldOf("ct_textures", Reference2ObjectMaps.emptyMap())
                    .forGetter(TextureEntry::textures)
    ).apply(inst, TextureEntry::new));
    public static final Codec<TextureEntry> CODEC = Codec.either(Identifier.CODEC, FULL_CODEC).xmap(
            either -> either.map(TextureEntry::new, Function.identity()),
            entry -> entry.textures.isEmpty() ? Either.left(entry.baseTexture) : Either.right(entry)
    );

    private TextureEntry(Identifier baseTexture)
    {
        this(baseTexture, Reference2ObjectMaps.emptyMap());
    }

    public Baked bake(MaterialBaker baker, TextureType type, TextureStrategy strategy)
    {
        Set<SpriteType> spriteTypes = strategy.computePermittedTypes(type.getSpriteTypes());
        Reference2ObjectMap<SpriteType, TextureAtlasSprite> sprites = new Reference2ObjectOpenHashMap<>(spriteTypes.size());
        for (SpriteType spriteType : spriteTypes)
        {
            Identifier texture = textures.get(spriteType);
            //noinspection ConstantValue
            if (texture == null)
            {
                texture = baseTexture.withSuffix("_" + spriteType.suffix());
            }
            sprites.put(spriteType, bakeSprite(baker, texture));
        }
        return new Baked(bakeSprite(baker, baseTexture), sprites);
    }

    private static TextureAtlasSprite bakeSprite(MaterialBaker baker, Identifier texture)
    {
        return baker.get(new Material(texture), () -> "").sprite();
    }

    @Nullable
    Set<SpriteType> validateSpriteTypes(TextureType type, TextureStrategy strategy)
    {
        ReferenceSet<SpriteType> usedTypes = textures.keySet();
        if (usedTypes.contains(SpriteType.NONE))
        {
            return Set.of(SpriteType.NONE);
        }
        Set<SpriteType> permittedTypes = strategy.computePermittedTypes(type.getSpriteTypes());
        if (!permittedTypes.containsAll(usedTypes))
        {
            return Sets.difference(usedTypes, permittedTypes);
        }
        return null;
    }

    public record Baked(TextureAtlasSprite baseSprite, Reference2ObjectMap<SpriteType, TextureAtlasSprite> sprites) implements SpriteLookup
    {
        @Override
        public TextureAtlasSprite get(SpriteType type)
        {
            return sprites.getOrDefault(type, baseSprite);
        }
    }
}
