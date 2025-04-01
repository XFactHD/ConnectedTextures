package xfacthd.contex.client.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.type.*;
import xfacthd.contex.api.utils.Utils;

import java.util.EnumSet;

public final class PillarTextureType extends DefaultTextureType
{
    public static final UV UV_NO_CON = new UV(0F, 0F, 1F, 1F);
    public static final UV UV_X = new UV(0F, .5F, 1F, 1F);
    public static final UV UV_Y = new UV(0F, 0F, 1F, .5F);
    public static final UV UV_Z_TOPBOTTOM = new UV(0F, 0F, 1F, .5F);
    public static final UV UV_Z_SIDE = new UV(0F, .5F, 1F, 1F);
    public static final PillarTextureType X = new PillarTextureType(Direction.Axis.X);
    public static final PillarTextureType Y = new PillarTextureType(Direction.Axis.Y);
    public static final PillarTextureType Z = new PillarTextureType(Direction.Axis.Z);

    private final Direction.Axis axis;
    private final Direction dirOne;
    private final Direction dirTwo;
    private final EnumSet<Direction> affectedFaces;
    private final UV uvHor;
    private final UV uvVert;

    private PillarTextureType(Direction.Axis axis)
    {
        this.axis = axis;
        this.dirOne = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        this.dirTwo = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        this.affectedFaces = EnumSet.complementOf(EnumSet.of(dirOne, dirTwo));
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
    public EnumSet<Direction> getAffectedFaces()
    {
        return affectedFaces;
    }

    @Override
    public byte getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    )
    {
        if (side.getAxis() == axis)
        {
            return 0;
        }

        ConnectionDirection conDirOne = ConnectionDirection.from(side, dirOne);
        ConnectionDirection conDirTwo = ConnectionDirection.from(side, dirTwo);
        BlockPos posOne = pos.relative(dirOne);
        BlockPos posTwo = pos.relative(dirTwo);

        byte connections = 0;
        if (predicate.test(level, pos, posOne, state, side, side) && isConnectionVisible(level, posOne, side, predicate, occlusionMode))
        {
            connections = set(connections, conDirOne);
        }
        if (predicate.test(level, pos, posTwo, state, side, side) && isConnectionVisible(level, posTwo, side, predicate, occlusionMode))
        {
            connections = set(connections, conDirTwo);
        }
        return connections;
    }

    @Override
    public UV getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if ((xCon || yCon))
        {
            return Utils.isY(side) ? uvVert : uvHor;
        }
        return UV_NO_CON;
    }
}
