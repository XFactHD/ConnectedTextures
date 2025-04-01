package xfacthd.contex.client.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.SimpleConnectionPredicate;

public final class MatchStatePredicate extends SimpleConnectionPredicate
{
    public static final MapCodec<MatchStatePredicate> CODEC = BlockState.CODEC
            .fieldOf("state")
            .xmap(MatchStatePredicate::new, pred -> pred.state);

    private final BlockState state;

    public MatchStatePredicate(BlockState state)
    {
        this.state = state;
    }

    @Override
    protected boolean compare(BlockState state, BlockState adjState)
    {
        return state == this.state && adjState == this.state;
    }

    @Override
    public MapCodec<MatchStatePredicate> codec()
    {
        return CODEC;
    }
}
