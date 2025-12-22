package io.github.xfacthd.contex.client.type;

import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.DefaultTextureType;
import io.github.xfacthd.contex.api.type.OcclusionMode;

import java.util.EnumSet;

public sealed class PillarTextureType extends DefaultTextureType permits RotatingPillarTextureType
{
    private static final BlockElementFace.UVs UV = new BlockElementFace.UVs(0F, 0F, 1F, 1F);
    public static final PillarTextureType X = new PillarTextureType(Direction.Axis.X);
    public static final PillarTextureType Y = new PillarTextureType(Direction.Axis.Y);
    public static final PillarTextureType Z = new PillarTextureType(Direction.Axis.Z);

    private final Direction.Axis axis;
    private final Direction dirOne;
    private final Direction dirTwo;
    private final EnumSet<Direction> affectedFaces;

    protected PillarTextureType(Direction.Axis axis)
    {
        this.axis = axis;
        this.dirOne = axis.getNegative();
        this.dirTwo = axis.getPositive();
        this.affectedFaces = EnumSet.complementOf(EnumSet.of(dirOne, dirTwo));
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
            connections = conDirOne.set(connections);
        }
        if (predicate.test(level, pos, posTwo, state, side, side) && isConnectionVisible(level, posTwo, side, predicate, occlusionMode))
        {
            connections = conDirTwo.set(connections);
        }
        return connections;
    }

    @Override
    public BlockElementFace.UVs getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        return UV;
    }
}
