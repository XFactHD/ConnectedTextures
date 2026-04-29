package io.github.xfacthd.contex.api.type;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SimpleConnectionPredicate implements ConnectionPredicate {
    @Override
    public final boolean test(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockPos otherPos,
            BlockState state,
            Direction side,
            Direction otherSide
    ) {
        BlockState otherState = level.getBlockState(otherPos);

        BlockState actualState = state.getAppearance(level, pos, side, otherState, otherPos);
        BlockState actualOtherState = otherState.getAppearance(level, otherPos, otherSide, state, pos);

        return !actualState.isAir() && compare(actualState, actualOtherState);
    }

    protected abstract boolean compare(BlockState state, BlockState adjState);
}
