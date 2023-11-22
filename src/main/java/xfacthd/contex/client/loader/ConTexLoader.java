package xfacthd.contex.client.loader;

import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Utils;
import xfacthd.contex.client.data.*;
import xfacthd.contex.api.type.TextureType;

import java.util.*;

public final class ConTexLoader implements IGeometryLoader<ConTexGeometry>
{
    @Override
    public ConTexGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
    {
        json.remove("loader");
        UnbakedModel baseModel = ctx.deserialize(json, BlockModel.class);

        JsonObject meta = GsonHelper.getAsJsonObject(json, "contex_meta");
        Map<ResourceLocation, TextureEntry> ctEntries = new HashMap<>(meta.size());
        meta.keySet().forEach(tex ->
        {
            JsonObject entry = GsonHelper.getAsJsonObject(meta, tex);

            ResourceLocation typeName = Utils.getAsLocation(entry, "type");
            TextureType type = MetadataRegistry.getType(
                    typeName, name -> new JsonSyntaxException("Unknown CT type: " + name)
            );

            ResourceLocation predName = Utils.getAsLocation(entry, "predicate", Builtin.Predicates.SAME_BLOCK);
            ConnectionPredicate predicate = MetadataRegistry.getPredicate(
                    predName, name -> new JsonSyntaxException("Unknown predicate: " + name)
            );

            ResourceLocation baseTex = new ResourceLocation(tex);
            ResourceLocation ctTex = TextureType.loadAdditionalTexture(type, json, baseTex);

            ctEntries.put(baseTex, new TextureEntry(type, baseTex, ctTex, predicate));
        });

        return new ConTexGeometry(baseModel, new Metadata(ctEntries));
    }
}
