package xfacthd.contex.client.data;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record TextureEntry(ResourceLocation baseTexture, @Nullable ResourceLocation ctTexture) { }
