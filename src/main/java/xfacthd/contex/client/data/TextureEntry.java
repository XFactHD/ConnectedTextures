package xfacthd.contex.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import xfacthd.contex.api.type.TextureType;

import java.util.Optional;

public record TextureEntry(Identifier baseTexture, Identifier auxTexture)
{
    private static final Codec<TextureEntry> FULL_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("main_texture").forGetter(TextureEntry::baseTexture),
            Identifier.CODEC.optionalFieldOf("ct_texture").forGetter(TextureEntry::ctTex)
    ).apply(inst, TextureEntry::of));
    public static final Codec<TextureEntry> CODEC = Codec.withAlternative(
            FULL_CODEC,
            Identifier.CODEC.xmap(TextureEntry::new, TextureEntry::baseTexture)
    );

    public TextureEntry(Identifier baseTexture)
    {
        this(baseTexture, baseTexture.withSuffix("_ctm"));
    }

    public Identifier get(TextureType type)
    {
        return type.hasAdditionalTexture() ? auxTexture : baseTexture;
    }

    private static TextureEntry of(Identifier baseTexture, Optional<Identifier> ctTexture)
    {
        return ctTexture.isPresent() ? new TextureEntry(baseTexture, ctTexture.get()) : new TextureEntry(baseTexture);
    }

    private Optional<Identifier> ctTex()
    {
        return auxTexture.equals(baseTexture.withSuffix("_ctm")) ? Optional.empty() : Optional.of(auxTexture);
    }
}
