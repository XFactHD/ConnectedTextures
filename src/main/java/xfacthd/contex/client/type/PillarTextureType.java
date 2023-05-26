package xfacthd.contex.client.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.state.ConnectionState;
import xfacthd.contex.api.type.*;
import xfacthd.contex.api.utils.Utils;

import java.util.EnumSet;

public final class PillarTextureType extends DefaultTextureType
{
    public static final UV UV_XY = new UV(0, 0, 16, 16);
    public static final UV UV_Z_TOPBOTTOM = new UV(0, 0, 16, 8);
    public static final UV UV_Z_SIDE = new UV(0, 8, 16, 16);

    private final Direction.Axis axis;
    private final Direction dirOne;
    private final Direction dirTwo;
    private final EnumSet<Direction> affectedFaces;

    public PillarTextureType(Direction.Axis axis)
    {
        this.axis = axis;
        this.dirOne = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        this.dirTwo = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        this.affectedFaces = EnumSet.complementOf(EnumSet.of(dirOne, dirTwo));
    }

    @Override
    public EnumSet<Direction> getAffectedFaces()
    {
        return affectedFaces;
    }

    @Override
    public ConnectionState getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            ResourceLocation texture
    )
    {
        if (side.getAxis() == axis)
        {
            return new ConnectionState(this, texture, (byte) 0);
        }

        ConnectionDirection conDirOne = ConnectionDirection.from(side, dirOne);
        ConnectionDirection conDirTwo = ConnectionDirection.from(side, dirTwo);

        byte connections = 0;
        if (predicate.test(level, pos, pos.relative(dirOne), state, conDirOne, side))
        {
            connections |= 1 << conDirOne.ordinal();
        }
        if (predicate.test(level, pos, pos.relative(dirTwo), state, conDirTwo, side))
        {
            connections |= 1 << conDirTwo.ordinal();
        }
        return new ConnectionState(this, texture, connections);
    }

    @Override
    public UV getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if ((xCon || yCon) && axis == Direction.Axis.Z)
        {
            return Utils.isY(side) ? UV_Z_TOPBOTTOM : UV_Z_SIDE;
        }
        return UV_XY;
    }
}
