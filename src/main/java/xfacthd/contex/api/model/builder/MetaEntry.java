package xfacthd.contex.api.model.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.type.OcclusionMode;

import java.util.*;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class MetaEntry
{
    private final ResourceLocation type;
    private final ExistingFileHelper existingFileHelper;
    private final Map<ResourceLocation, TextureEntry> textures = new LinkedHashMap<>();
    @Nullable
    private ResourceLocation predicate;
    @Nullable
    private OcclusionMode occlusionMode;

    MetaEntry(ResourceLocation type, ExistingFileHelper existingFileHelper)
    {
        this.type = type;
        this.existingFileHelper = existingFileHelper;
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
        if (!existingFileHelper.exists(baseTexture, ModelProvider.TEXTURE))
        {
            throw new IllegalArgumentException("Base texture '" + baseTexture + "' does not exist in any known resource pack");
        }
        if (ctTexture != null && !existingFileHelper.exists(ctTexture, ModelProvider.TEXTURE))
        {
            throw new IllegalArgumentException("CT texture '" + ctTexture + "' does not exist in any known resource pack");
        }
        if (textures.containsKey(baseTexture))
        {
            throw new IllegalStateException("Duplicate registration of texture: " + baseTexture);
        }
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
