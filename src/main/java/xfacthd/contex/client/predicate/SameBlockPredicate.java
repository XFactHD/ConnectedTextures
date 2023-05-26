package xfacthd.contex.client.predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.state.ConnectionDirection;

public class SameBlockPredicate implements ConnectionPredicate
{
    @Override
    public boolean test(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockPos otherPos,
            BlockState state,
            ConnectionDirection conDir,
            Direction side
    )
    {
        BlockState otherState = level.getBlockState(otherPos);

        Block block = state.getAppearance(level, pos, side, otherState, otherPos).getBlock();
        Block otherBlock = otherState.getAppearance(level, otherPos, side, state, pos).getBlock();

        return block == otherBlock;
    }
}
