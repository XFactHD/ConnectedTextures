package xfacthd.contex.api.state;

import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.type.TextureType;

public record ConnectionState(TextureType type, ResourceLocation texture, byte connections)
{
    public boolean isSet(ConnectionDirection dir)
    {
        return (connections & (1 << dir.ordinal())) != 0;
    }
}
