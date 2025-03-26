package xfacthd.contex.api.datagen;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.OcclusionMode;
import xfacthd.contex.api.type.TextureType;
import xfacthd.contex.client.data.MetaEntry;
import xfacthd.contex.client.data.TextureEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MetaEntryBuilder
{
    @Nullable
    private TextureType type;
    @Nullable
    private ConnectionPredicate predicate;
    private OcclusionMode occlusionMode = OcclusionMode.SELF;
    private final List<TextureEntry> textures = new ArrayList<>();

    MetaEntryBuilder() { }

    public MetaEntryBuilder type(TextureType type)
    {
        this.type = type;
        return this;
    }

    public MetaEntryBuilder predicate(ConnectionPredicate predicate)
    {
        this.predicate = predicate;
        return this;
    }

    public MetaEntryBuilder occlusionMode(OcclusionMode occlusionMode)
    {
        this.occlusionMode = occlusionMode;
        return this;
    }

    public MetaEntryBuilder addTexture(ResourceLocation texture)
    {
        return addTexture(texture, texture.withSuffix("_ctm"));
    }

    public MetaEntryBuilder addTexture(ResourceLocation baseTexture, ResourceLocation ctTexture)
    {
        textures.add(new TextureEntry(baseTexture, ctTexture));
        return this;
    }

    MetaEntry build()
    {
        Objects.requireNonNull(type, "No TextureType specified");
        Objects.requireNonNull(predicate, "No ConnectionPredicate specified");
        return new MetaEntry(type, predicate, occlusionMode, textures.toArray(TextureEntry[]::new));
    }
}
