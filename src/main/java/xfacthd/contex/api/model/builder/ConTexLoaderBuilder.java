package xfacthd.contex.api.model.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Utils;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class ConTexLoaderBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T>
{
    private static final ResourceLocation ID = Utils.rl("loader");

    private final List<MetaEntry> metaEntries = new ArrayList<>();

    public ConTexLoaderBuilder(T parent, ExistingFileHelper fileHelper)
    {
        super(ID, parent, fileHelper, true);
    }

    /**
     * Add a CT metadata entry with the given CT type, affecting a single texture
     * @param type    The CT type to use ({@link Builtin.Types} for builtin CT types)
     * @param texture The target texture
     */
    public ConTexLoaderBuilder<T> addCtEntry(ResourceLocation type, ResourceLocation texture)
    {
        return addCtEntry(type, e -> e.addTexture(texture));
    }

    /**
     * Add a CT metadata entry with the given CT type and predicate, affecting a single texture
     * @param type      The CT type to use ({@link Builtin.Types} for builtin CT types)
     * @param texture   The target texture
     * @param predicate The predicate to use for connection checks ({@link Builtin.Predicates} for builtin predicates)
     */
    public ConTexLoaderBuilder<T> addCtEntry(ResourceLocation type, ResourceLocation texture, ResourceLocation predicate)
    {
        return addCtEntry(type, e -> e.addTexture(texture).predicate(predicate));
    }

    /**
     * Add a CT metadata entry with the given CT type, to be configured via the given configurator
     * @param type The CT type to use ({@link Builtin.Types} for builtin CT types)
     * @param configurator A configurator for the added entry for further configuration
     */
    public ConTexLoaderBuilder<T> addCtEntry(ResourceLocation type, Consumer<MetaEntry> configurator)
    {
        MetaEntry entry = new MetaEntry(type, existingFileHelper);
        configurator.accept(entry);
        metaEntries.add(entry);

        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);
        if (metaEntries.isEmpty()) return json;

        JsonArray ctMeta = new JsonArray();
        metaEntries.forEach(meta -> ctMeta.add(meta.toJson()));
        json.add("contex_meta", ctMeta);

        return json;
    }
}
