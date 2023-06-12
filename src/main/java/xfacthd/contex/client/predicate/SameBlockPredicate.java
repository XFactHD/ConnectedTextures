package xfacthd.contex.client.predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
            ConnectionDirection otherConDir,
            Direction side,
            Direction otherSide
    )
    {
        BlockState otherState = level.getBlockState(otherPos);

        BlockState actualState = state.getAppearance(level, pos, side, otherState, otherPos);
        BlockState actualOtherState = otherState.getAppearance(level, otherPos, otherSide, state, pos);

        return !actualState.isAir() && actualState.getBlock() == actualOtherState.getBlock();
    }
}
