package xfacthd.contex.client.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.OcclusionMode;

public final class SimpleCarpetTextureType extends SimpleTextureType
{
    public static final SimpleCarpetTextureType Y = new SimpleCarpetTextureType(Direction.Axis.Y);
    public static final SimpleCarpetTextureType X = new SimpleCarpetTextureType(Direction.Axis.X);
    public static final SimpleCarpetTextureType Z = new SimpleCarpetTextureType(Direction.Axis.Z);

    private final Direction.Axis axis;

    private SimpleCarpetTextureType(Direction.Axis axis)
    {
        this.axis = axis;
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
            return super.getConnectionState(level, pos, state, side, predicate, OcclusionMode.NONE);
        }
        return 0;
    }
}
