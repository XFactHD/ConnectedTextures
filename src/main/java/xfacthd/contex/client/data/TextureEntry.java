package xfacthd.contex.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.type.TextureType;

import java.util.Optional;

public record TextureEntry(ResourceLocation baseTexture, ResourceLocation auxTexture)
{
    private static final Codec<TextureEntry> FULL_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("main_texture").forGetter(TextureEntry::baseTexture),
            ResourceLocation.CODEC.optionalFieldOf("ct_texture").forGetter(TextureEntry::ctTex)
    ).apply(inst, TextureEntry::of));
    public static final Codec<TextureEntry> CODEC = Codec.withAlternative(
            FULL_CODEC,
            ResourceLocation.CODEC.xmap(TextureEntry::new, TextureEntry::baseTexture)
    );

    public TextureEntry(ResourceLocation baseTexture)
    {
        this(baseTexture, baseTexture.withSuffix("_ctm"));
    }

    public ResourceLocation get(TextureType type)
    {
        return type.hasAdditionalTexture() ? auxTexture : baseTexture;
    }

    private static TextureEntry of(ResourceLocation baseTexture, Optional<ResourceLocation> ctTexture)
    {
        return ctTexture.isPresent() ? new TextureEntry(baseTexture, ctTexture.get()) : new TextureEntry(baseTexture);
    }

    private Optional<ResourceLocation> ctTex()
    {
        return auxTexture.equals(baseTexture.withSuffix("_ctm")) ? Optional.empty() : Optional.of(auxTexture);
    }
}
