package xfacthd.contex.client.loader;

import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import xfacthd.contex.api.type.*;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Utils;
import xfacthd.contex.client.data.*;

import java.util.*;

public final class ConTexLoader implements IGeometryLoader<ConTexGeometry>
{
    @Override
    public ConTexGeometry read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
    {
        json.remove("loader");
        UnbakedModel baseModel = ctx.deserialize(json, BlockModel.class);

        JsonArray meta = GsonHelper.getAsJsonArray(json, "contex_meta");
        List<MetaEntry> ctEntries = new ArrayList<>(meta.size());
        for (int i = 0; i < meta.size(); i++)
        {
            JsonObject entry = GsonHelper.convertToJsonObject(meta.get(i), "contex_meta[" + i + "]");

            ResourceLocation typeName = Utils.getAsLocation(entry, "type");
            TextureType type = MetadataRegistry.getType(
                    typeName, name -> new JsonSyntaxException("Unknown CT type: " + name)
            );

            ResourceLocation predName = Utils.getAsLocation(entry, "predicate", Builtin.Predicates.SAME_BLOCK);
            ConnectionPredicate predicate = MetadataRegistry.getPredicate(
                    predName, name -> new JsonSyntaxException("Unknown CT predicate: " + name)
            );

            String mode = GsonHelper.getAsString(entry, "occlusion_mode", "SELF");
            OcclusionMode occlusionMode;
            try
            {
                occlusionMode = OcclusionMode.valueOf(mode);
            }
            catch (IllegalArgumentException e)
            {
                throw new JsonParseException("Invalid occlusion mode: " + mode, e);
            }

            JsonArray textures = GsonHelper.getAsJsonArray(entry, "textures");
            List<TextureEntry> textureEntries = new ArrayList<>(textures.size());
            for (int j = 0; j < textures.size(); j++)
            {
                JsonElement texEntry = textures.get(i);
                ResourceLocation baseTex;
                ResourceLocation ctTex = null;
                if (texEntry.isJsonPrimitive())
                {
                    baseTex = Utils.convertToLocation(texEntry, "textures[" + j + "]");
                    if (type.hasAdditionalTexture())
                    {
                        ctTex = baseTex.withSuffix("_ctm");
                    }
                }
                else
                {
                    JsonObject texObj = GsonHelper.convertToJsonObject(texEntry, "textures[" + j + "]");
                    baseTex = Utils.getAsLocation(texObj, TextureType.BASE_TEXTURE_KEY);
                    if (type.hasAdditionalTexture())
                    {
                        if (entry.has(TextureType.ADDITIONAL_TEXTURE_KEY))
                        {
                            ctTex = Utils.getAsLocation(entry, TextureType.ADDITIONAL_TEXTURE_KEY);
                        }
                        else
                        {
                            ctTex = baseTex.withSuffix("_ctm");
                        }
                    }
                }

                textureEntries.add(new TextureEntry(baseTex, ctTex));
            }

            ctEntries.add(new MetaEntry(type, predicate, occlusionMode, textureEntries.toArray(TextureEntry[]::new)));
        }

        Map<ResourceLocation, MetaEntry> uniqueTextures = new HashMap<>();
        for (int metaIdx = 0; metaIdx < ctEntries.size(); metaIdx++)
        {
            MetaEntry entry = ctEntries.get(metaIdx);
            TextureEntry[] textures = entry.textures();
            for (int texIdx = 0; texIdx < textures.length; texIdx++)
            {
                TextureEntry texture = textures[texIdx];
                MetaEntry lastEntry = uniqueTextures.put(texture.baseTexture(), entry);
                if (lastEntry != null)
                {
                    throw new JsonParseException(
                            "Found duplicate texture '%s' in meta entry contex_meta[%d].textures[%d], previously found in meta entry contex_meta[%d]".formatted(
                                    texture.baseTexture(), metaIdx, texIdx, ctEntries.indexOf(entry)
                            )
                    );
                }
            }
        }

        return new ConTexGeometry(baseModel, ctEntries);
    }
}
