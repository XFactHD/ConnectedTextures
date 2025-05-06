package xfacthd.contex.api.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.client.model.IQuadTransformer;

import java.util.Arrays;

public final class QuadData
{
    final BakedQuad quad;
    final int[] vertexData;
    final boolean uvRotated;
    TextureAtlasSprite sprite;

    public QuadData(BakedQuad quad)
    {
        this.quad = quad;
        int[] vertexData = quad.vertices();
        this.vertexData = Arrays.copyOf(vertexData, vertexData.length);
        this.uvRotated = ModelUtils.isQuadRotated(this);
        this.sprite = quad.sprite();
    }

    QuadData(QuadData data)
    {
        this.quad = data.quad;
        this.vertexData = Arrays.copyOf(data.vertexData, data.vertexData.length);
        this.uvRotated = data.uvRotated;
        this.sprite = data.sprite;
    }

    public BakedQuad quad()
    {
        return quad;
    }

    public boolean isUvRotated()
    {
        return uvRotated;
    }

    public TextureAtlasSprite sprite()
    {
        return sprite;
    }

    public void sprite(TextureAtlasSprite sprite)
    {
        this.sprite = sprite;
    }

    public float pos(int vert, int idx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        return Float.intBitsToFloat(vertexData[offset + idx]);
    }

    public void pos(int vert, int idx, float val)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        vertexData[offset + idx] = Float.floatToRawIntBits(val);
    }

    public float uv(int vert, int idx)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        return Float.intBitsToFloat(vertexData[offset + idx]);
    }

    public void uv(int vert, int idx, float val)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertexData[offset + idx] = Float.floatToRawIntBits(val);
    }
}
