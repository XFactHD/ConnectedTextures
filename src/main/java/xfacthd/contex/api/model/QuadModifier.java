package xfacthd.contex.api.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Arrays;

// TODO: update to improved version from FramedBlocks
public final class QuadModifier
{
    private static final QuadModifier FAILED = new QuadModifier(null, true);

    private final Data data;
    private boolean failed;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can only modify vertex position, texture and normals
     */
    public static QuadModifier of(BakedQuad quad)
    {
        float[][] pos = new float[4][3];
        float[][] uv = new float[4][2];

        int[] vertexData = quad.getVertices();
        for (int i = 0; i < 4; i++)
        {
            ModelUtils.unpackPosition(vertexData, pos[i], i);
            ModelUtils.unpackUV(vertexData, uv[i], i);
        }

        return new QuadModifier(new Data(quad, pos, uv), false);
    }

    private QuadModifier(Data data, boolean failed)
    {
        this.data = data;
        this.failed = failed;
    }

    /**
     * Apply the given {@link Modifier} to the current vertex data. If a previous modifier failed,
     * then the modification will not be applied
     */
    public QuadModifier apply(Modifier modifier)
    {
        if (!failed)
        {
            failed = !modifier.accept(data);
        }
        return this;
    }

    /**
     * Re-assemble a copy of the quad and export it to the given quad consumer. If any of the modifiers failed,
     * the quad will not be exported
     */
    public BakedQuad export()
    {
        if (failed)
        {
            return null;
        }

        int[] vertexData = data.quad.getVertices();
        vertexData = Arrays.copyOf(vertexData, vertexData.length);
        packVertexData(vertexData);

        return new BakedQuad(
                vertexData,
                data.quad.getTintIndex(),
                data.quad.getDirection(),
                data.sprite.getValue(),
                data.quad.isShade(),
                data.quad.hasAmbientOcclusion()
        );
    }

    private void packVertexData(int[] vertexData)
    {
        for (int i = 0; i < 4; i++)
        {
            ModelUtils.packPosition(data.pos[i], vertexData, i);
            ModelUtils.packUV(data.uv[i], vertexData, i);
        }
    }

    /**
     * Clone this {@code QuadModifier} to continue modifying the source quad in multiple different ways without
     * having to repeat the equivalent modification steps
     * @return a new {@code QuadModifier} with a deep-copy of the current data or an empty,
     * failed modifier if this modifier previously failed
     */
    public QuadModifier derive()
    {
        if (failed)
        {
            return FAILED;
        }
        return new QuadModifier(new Data(data), false);
    }

    public boolean hasFailed()
    {
        return failed;
    }



    public record Data(BakedQuad quad, float[][] pos, float[][] uv, MutableObject<TextureAtlasSprite> sprite)
    {
        public Data(BakedQuad quad, float[][] pos, float[][] uv)
        {
            this(quad, pos, uv, new MutableObject<>(quad.getSprite()));
        }

        @SuppressWarnings("CopyConstructorMissesField")
        public Data(Data data)
        {
            this(
                    data.quad,
                    deepCopy(data.pos),
                    deepCopy(data.uv),
                    new MutableObject<>(data.sprite.getValue())
            );
        }

        private static float[][] deepCopy(float[][] arr)
        {
            float[][] newArr = new float[arr.length][];
            for (int i = 0; i < arr.length; i++)
            {
                newArr[i] = Arrays.copyOf(arr[i], arr[i].length);
            }
            return newArr;
        }
    }

    @FunctionalInterface
    public interface Modifier
    {
        boolean accept(Data data);
    }
}
