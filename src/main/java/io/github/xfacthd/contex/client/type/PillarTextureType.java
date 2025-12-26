package io.github.xfacthd.contex.client.type;

import io.github.xfacthd.contex.api.type.SpriteType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.DefaultTextureType;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public sealed class PillarTextureType extends DefaultTextureType permits RotatingPillarTextureType
{
    public static final PillarTextureType X = new PillarTextureType(Direction.Axis.X);
    public static final PillarTextureType Y = new PillarTextureType(Direction.Axis.Y);
    public static final PillarTextureType Z = new PillarTextureType(Direction.Axis.Z);
    private static final EnumSet<SpriteType> SPRITE_TYPES = EnumSet.of(SpriteType.VERTICAL);

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
    @Nullable
    public SpriteType getConnectedSprite(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        return xCon || yCon ? SpriteType.VERTICAL : null;
    }

    @Override
    public EnumSet<SpriteType> getSpriteTypes()
    {
        return SPRITE_TYPES;
    }
}
