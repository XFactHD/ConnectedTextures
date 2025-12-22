package io.github.xfacthd.contex.client.type;

import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.core.Direction;
import io.github.xfacthd.contex.api.utils.Utils;

public final class RotatingPillarTextureType extends PillarTextureType
{
    static final BlockElementFace.UVs UV_NO_CON = new BlockElementFace.UVs(0F, 0F, 1F, 1F);
    static final BlockElementFace.UVs UV_X = new BlockElementFace.UVs(0F, .5F, 1F, 1F);
    static final BlockElementFace.UVs UV_Y = new BlockElementFace.UVs(0F, 0F, 1F, .5F);
    static final BlockElementFace.UVs UV_Z_TOPBOTTOM = new BlockElementFace.UVs(0F, 0F, 1F, .5F);
    static final BlockElementFace.UVs UV_Z_SIDE = new BlockElementFace.UVs(0F, .5F, 1F, 1F);
    public static final RotatingPillarTextureType X = new RotatingPillarTextureType(Direction.Axis.X);
    public static final RotatingPillarTextureType Y = new RotatingPillarTextureType(Direction.Axis.Y);
    public static final RotatingPillarTextureType Z = new RotatingPillarTextureType(Direction.Axis.Z);

    private final BlockElementFace.UVs uvHor;
    private final BlockElementFace.UVs uvVert;

    private RotatingPillarTextureType(Direction.Axis axis)
    {
        super(axis);
        switch (axis)
        {
            case X -> uvHor = uvVert = UV_X;
            case Y -> uvHor = uvVert = UV_Y;
            case Z ->
            {
                uvHor = UV_Z_SIDE;
                uvVert = UV_Z_TOPBOTTOM;
            }
            default -> throw new AssertionError();
        }
    }

    @Override
    public BlockElementFace.UVs getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if ((xCon || yCon))
        {
            return Utils.isY(side) ? uvVert : uvHor;
        }
        return UV_NO_CON;
    }
}
