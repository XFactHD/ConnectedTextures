package xfacthd.contex.client.data;

import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.type.*;

public record MetaEntry(TextureType type, ConnectionPredicate predicate, OcclusionMode occlusionMode, TextureEntry[] textures)
{
    public TextureEntry texture(int texIdx)
    {
        return textures[texIdx];
    }

    public int findTexture(ResourceLocation tex)
    {
        for (int i = 0; i < textures.length; i++)
        {
            TextureEntry texture = textures[i];
            if (texture.baseTexture().equals(tex))
            {
                return i;
            }
        }
        return -1;
    }
}
