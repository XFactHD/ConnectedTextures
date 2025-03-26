package xfacthd.contex.client.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.ConnectionPredicate;

public class SameStatePredicate implements ConnectionPredicate
{
    public static final SameStatePredicate INSTANCE = new SameStatePredicate();
    public static final MapCodec<SameStatePredicate> CODEC = MapCodec.unit(INSTANCE);

    private SameStatePredicate() { }

    @Override
    public boolean test(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockPos otherPos,
            BlockState state,
            Direction side,
            Direction otherSide
    )
    {
        BlockState otherState = level.getBlockState(otherPos);

        BlockState actualState = state.getAppearance(level, pos, side, otherState, otherPos);
        BlockState actualOtherState = otherState.getAppearance(level, otherPos, otherSide, state, pos);

        return !actualState.isAir() && actualState == actualOtherState;
    }

    @Override
    public MapCodec<SameStatePredicate> codec()
    {
        return CODEC;
    }
}
