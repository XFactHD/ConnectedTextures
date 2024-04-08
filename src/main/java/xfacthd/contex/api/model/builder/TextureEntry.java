package xfacthd.contex.api.model.builder;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.type.TextureType;

record TextureEntry(ResourceLocation baseTexture, @Nullable ResourceLocation ctTexture)
{
    JsonElement toJson()
    {
        if (ctTexture == null)
        {
            return new JsonPrimitive(baseTexture.toString());
        }

        JsonObject json = new JsonObject();
        json.addProperty(TextureType.BASE_TEXTURE_KEY, baseTexture.toString());
        json.addProperty(TextureType.ADDITIONAL_TEXTURE_KEY, ctTexture.toString());
        return json;
    }
}
