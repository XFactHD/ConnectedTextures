package xfacthd.contex.client.data;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.TextureType;

public record TextureEntry(
        TextureType type,
        ResourceLocation baseTexture,
        @Nullable ResourceLocation ctTexture,
        ConnectionPredicate predicate
)
{

}
