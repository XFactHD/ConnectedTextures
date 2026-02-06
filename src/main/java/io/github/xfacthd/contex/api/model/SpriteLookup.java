package io.github.xfacthd.contex.api.model;

import io.github.xfacthd.contex.api.type.SpriteType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface SpriteLookup
{
    TextureAtlasSprite get(SpriteType type);
}
