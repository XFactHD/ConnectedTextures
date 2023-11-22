package xfacthd.contex.api.model;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.TextureType;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class ConTexLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    private static final ResourceLocation ID = Utils.rl("loader");

    private final Map<ResourceLocation, TextureEntry> textures = new LinkedHashMap<>();

    public ConTexLoaderBuilder(T parent, ExistingFileHelper fileHelper)
    {
        super(ID, parent, fileHelper, true);
    }

    /**
     * Add a metadata entry for the given texture with the given CT type
     * @param texture The target texture
     * @param type The CT type to use ({@link Builtin.Types} for builtin CT types)
     */
    public ConTexLoaderBuilder<T> connectedTexture(ResourceLocation texture, ResourceLocation type)
    {
        return connectedTexture(texture, type, e -> {});
    }

    /**
     * Add a metadata entry for the given texture with the given CT type
     * @param texture The target texture
     * @param type The CT type to use ({@link Builtin.Types} for builtin CT types)
     * @param configurator A configurator for the added entry for further configuration
     */
    public ConTexLoaderBuilder<T> connectedTexture(
            ResourceLocation texture, ResourceLocation type, Consumer<TextureEntry> configurator
    )
    {
        if (!existingFileHelper.exists(texture, ModelProvider.TEXTURE))
        {
            throw new IllegalArgumentException("Texture '" + texture + "' does not exist in any known resource pack");
        }
        if (textures.containsKey(texture))
        {
            throw new IllegalStateException("Duplicate registration of texture: " + texture);
        }

        TextureEntry entry = new TextureEntry(type);
        configurator.accept(entry);
        textures.put(texture, entry);

        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);
        if (textures.isEmpty()) return json;

        JsonObject ctMeta = new JsonObject();
        textures.forEach((tex, meta) -> ctMeta.add(tex.toString(), meta.toJson()));
        json.add("contex_meta", ctMeta);

        return json;
    }



    public static final class TextureEntry
    {
        private final ResourceLocation type;
        @Nullable
        private ResourceLocation predicate;
        @Nullable
        private ResourceLocation additionalTextureValue;

        TextureEntry(ResourceLocation type)
        {
            this.type = type;
        }

        /**
         * Set the {@link ConnectionPredicate} to use for calculating connections.
         * See {@link Builtin.Predicates} for the builtin connection predicates
         */
        public TextureEntry predicate(ResourceLocation predicate)
        {
            this.predicate = Preconditions.checkNotNull(predicate);
            return this;
        }

        /**
         * Set an additional texture to use with the configured CT type
         */
        public TextureEntry additionalTexture(ResourceLocation texture)
        {
            this.additionalTextureValue = Preconditions.checkNotNull(texture);
            return this;
        }

        JsonObject toJson()
        {
            JsonObject json = new JsonObject();
            json.addProperty("type", type.toString());
            if (predicate != null)
            {
                json.addProperty("predicate", predicate.toString());
            }
            if (additionalTextureValue != null)
            {
                json.addProperty(TextureType.ADDITIONAL_TEXTURE_KEY, additionalTextureValue.toString());
            }
            return json;
        }
    }
}
