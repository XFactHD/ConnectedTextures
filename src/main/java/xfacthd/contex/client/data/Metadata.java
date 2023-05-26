package xfacthd.contex.client.data;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public record Metadata(Map<ResourceLocation, TextureEntry> entries)
{
    public Collection<TextureEntry> getAll()
    {
        return entries.values();
    }

    public TextureEntry get(ResourceLocation tex)
    {
        return entries.get(tex);
    }
}
