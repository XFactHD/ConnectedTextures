package xfacthd.contex.api.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.utils.Utils;

public final class ModelUtils
{
    @SuppressWarnings("deprecation")
    public static TextureAtlasSprite getSprite(ResourceLocation loc)
    {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(loc);
    }

    public static UVInfo getUVInfo(Direction face)
    {
        int uIdx;
        int vIdx;
        boolean uInv;
        boolean vInv;
        if (Utils.isY(face))
        {
            uIdx = 0;
            vIdx = 2;

            uInv = false;
            vInv = face == Direction.DOWN;
        }
        else
        {
            uIdx = Utils.isX(face) ? 2 : 0;
            vIdx = 1;

            uInv = Utils.isPositive(face.getClockWise());
            vInv = true;
        }
        return new UVInfo(uIdx, vIdx, uInv, vInv);
    }



    private ModelUtils() { }
}