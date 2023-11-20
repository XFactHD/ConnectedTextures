package xfacthd.contex.api.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import xfacthd.contex.api.utils.Utils;

public final class ModelUtils
{
    public static void unpackPosition(int[] vertexData, float[] pos, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        pos[0] = Float.intBitsToFloat(vertexData[offset]);
        pos[1] = Float.intBitsToFloat(vertexData[offset + 1]);
        pos[2] = Float.intBitsToFloat(vertexData[offset + 2]);
    }

    public static void unpackUV(int[] vertexData, float[] uv, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        uv[0] = Float.intBitsToFloat(vertexData[offset]);
        uv[1] = Float.intBitsToFloat(vertexData[offset + 1]);
    }

    public static void packPosition(float[] pos, int[] vertexData, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        vertexData[offset    ] = Float.floatToRawIntBits(pos[0]);
        vertexData[offset + 1] = Float.floatToRawIntBits(pos[1]);
        vertexData[offset + 2] = Float.floatToRawIntBits(pos[2]);
    }

    public static void packUV(float[] uv, int[] vertexData, int vert)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertexData[offset    ] = Float.floatToRawIntBits(uv[0]);
        vertexData[offset + 1] = Float.floatToRawIntBits(uv[1]);
    }

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