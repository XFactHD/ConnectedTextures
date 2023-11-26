package xfacthd.contex.api.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.client.model.IQuadTransformer;

import java.util.Arrays;

public final class QuadData
{
    final BakedQuad quad;
    final int[] vertexData;
    TextureAtlasSprite sprite;

    public QuadData(BakedQuad quad)
    {
        this.quad = quad;
        int[] vertexData = quad.getVertices();
        this.vertexData = Arrays.copyOf(vertexData, vertexData.length);
    }

    QuadData(QuadData data)
    {
        this.quad = data.quad;
        this.vertexData = Arrays.copyOf(data.vertexData, data.vertexData.length);
        this.sprite = data.sprite;
    }

    public BakedQuad quad()
    {
        return quad;
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

    public void uv(int vert, float u, float v)
    {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertexData[offset] = Float.floatToRawIntBits(u);
        vertexData[offset + 1] = Float.floatToRawIntBits(v);
    }
}
