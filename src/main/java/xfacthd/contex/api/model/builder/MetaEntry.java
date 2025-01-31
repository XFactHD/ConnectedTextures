package xfacthd.contex.api.model.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.type.OcclusionMode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class MetaEntry
{
    private final ResourceLocation type;
    private final Map<ResourceLocation, TextureEntry> textures = new LinkedHashMap<>();
    @Nullable
    private ResourceLocation predicate;
    @Nullable
    private OcclusionMode occlusionMode;

    MetaEntry(ResourceLocation type)
    {
        this.type = type;
    }

    /**
     * Set the predicate to use for connection checks
     */
    public MetaEntry predicate(ResourceLocation predicate)
    {
        this.predicate = Objects.requireNonNull(predicate);
        return this;
    }

    /**
     * Set the occlusion mode to use for connection occlusion checks
     */
    public MetaEntry occlusionMode(OcclusionMode occlusionMode)
    {
        this.occlusionMode = Objects.requireNonNull(occlusionMode);
        return this;
    }

    /**
     * Add a texture to be affected by this CT entry
     */
    public MetaEntry addTexture(ResourceLocation texture)
    {
        Objects.requireNonNull(texture);
        validateTexture(texture, null);
        textures.put(texture, new TextureEntry(texture, null));
        return this;
    }

    /**
     * Add a texture with the given additional texture (if used by the CT type) to be affected by this CT entry
     */
    public MetaEntry addTexture(ResourceLocation baseTexture, ResourceLocation ctTexture)
    {
        Objects.requireNonNull(baseTexture);
        Objects.requireNonNull(ctTexture);
        validateTexture(baseTexture, ctTexture);
        textures.put(baseTexture, new TextureEntry(baseTexture, ctTexture));
        return this;
    }

    private void validateTexture(ResourceLocation baseTexture, @Nullable ResourceLocation ctTexture)
    {
        if (textures.containsKey(baseTexture))
        {
            throw new IllegalStateException("Duplicate registration of texture: " + baseTexture);
        }
    }

    MetaEntry copy()
    {
        MetaEntry metaEntry = new MetaEntry(type);
        metaEntry.textures.putAll(textures);
        metaEntry.predicate = predicate;
        metaEntry.occlusionMode = occlusionMode;
        return metaEntry;
    }

    JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", type.toString());
        if (predicate != null)
        {
            obj.addProperty("predicate", predicate.toString());
        }
        if (occlusionMode != null)
        {
            obj.addProperty("occlusion_mode", occlusionMode.toString());
        }

        JsonArray texArray = new JsonArray();
        textures.values().forEach(tex -> texArray.add(tex.toJson()));
        obj.add("textures", texArray);

        return obj;
    }
}
