package xfacthd.contex.api.model;

import net.minecraft.Optionull;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class QuadData
{
    final BakedQuad quad;
    private final Vector3fc pos0;
    private final Vector3fc pos1;
    private final Vector3fc pos2;
    private final Vector3fc pos3;
    @Nullable
    private Vector3f mutPos0;
    @Nullable
    private Vector3f mutPos1;
    @Nullable
    private Vector3f mutPos2;
    @Nullable
    private Vector3f mutPos3;
    private long uv0;
    private long uv1;
    private long uv2;
    private long uv3;
    final boolean uvRotated;
    TextureAtlasSprite sprite;

    public QuadData(BakedQuad quad)
    {
        this.quad = quad;
        this.pos0 = quad.position0();
        this.pos1 = quad.position1();
        this.pos2 = quad.position2();
        this.pos3 = quad.position3();
        this.uv0 = quad.packedUV0();
        this.uv1 = quad.packedUV1();
        this.uv2 = quad.packedUV2();
        this.uv3 = quad.packedUV3();
        this.uvRotated = ModelUtils.isQuadRotated(this);
        this.sprite = quad.sprite();
    }

    QuadData(QuadData data)
    {
        this.quad = data.quad;
        this.pos0 = data.pos0;
        this.pos1 = data.pos1;
        this.pos2 = data.pos2;
        this.pos3 = data.pos3;
        this.mutPos0 = Optionull.map(data.mutPos0, Vector3f::new);
        this.mutPos1 = Optionull.map(data.mutPos1, Vector3f::new);
        this.mutPos2 = Optionull.map(data.mutPos2, Vector3f::new);
        this.mutPos3 = Optionull.map(data.mutPos3, Vector3f::new);
        this.uv0 = data.uv0;
        this.uv1 = data.uv1;
        this.uv2 = data.uv2;
        this.uv3 = data.uv3;
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
        return pos(vert).get(idx);
    }

    public void pos(int vert, int idx, float val)
    {
        (switch (vert)
        {
            case 0 -> mutPos0 != null ? mutPos0 : (mutPos0 = new Vector3f(pos0));
            case 1 -> mutPos1 != null ? mutPos1 : (mutPos1 = new Vector3f(pos1));
            case 2 -> mutPos2 != null ? mutPos2 : (mutPos2 = new Vector3f(pos2));
            case 3 -> mutPos3 != null ? mutPos3 : (mutPos3 = new Vector3f(pos3));
            default -> throw new IndexOutOfBoundsException(vert);
        }).setComponent(idx, val);
    }

    public float uv(int vert, int idx)
    {
        long packed = switch (vert)
        {
            case 0 -> uv0;
            case 1 -> uv1;
            case 2 -> uv2;
            case 3 -> uv3;
            default -> throw new IndexOutOfBoundsException(vert);
        };
        int masked = (int) (packed >> (32 * (1 - idx)) & 0xFFFFFFFFL);
        return Float.intBitsToFloat(masked);
    }

    public void uv(int vert, int idx, float val)
    {
        float u = idx == 0 ? val : uv(vert, 0);
        float v = idx == 1 ? val : uv(vert, 1);
        uv(vert, u, v);
    }

    public void uv(int vert, float u, float v)
    {
        long packed = UVPair.pack(u, v);
        switch (vert)
        {
            case 0 -> uv0 = packed;
            case 1 -> uv1 = packed;
            case 2 -> uv2 = packed;
            case 3 -> uv3 = packed;
            default -> throw new IndexOutOfBoundsException(vert);
        }
    }

    private Vector3fc pos(int vert)
    {
        return switch (vert)
        {
            case 0 -> Objects.requireNonNullElse(mutPos0, pos0);
            case 1 -> Objects.requireNonNullElse(mutPos1, pos1);
            case 2 -> Objects.requireNonNullElse(mutPos2, pos2);
            case 3 -> Objects.requireNonNullElse(mutPos3, pos3);
            default -> throw new IndexOutOfBoundsException(vert);
        };
    }

    BakedQuad toQuad() {
        return new BakedQuad(
                pos(0),
                pos(1),
                pos(2),
                pos(3),
                uv0,
                uv1,
                uv2,
                uv3,
                quad.tintIndex(),
                quad.direction(),
                sprite,
                quad.shade(),
                quad.lightEmission(),
                quad.bakedNormals(),
                quad.bakedColors(),
                quad.hasAmbientOcclusion()
        );
    }
}
