package io.github.xfacthd.contex.client.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.type.SimpleConnectionPredicate;

public final class SameBlockPredicate extends SimpleConnectionPredicate
{
    public static final SameBlockPredicate INSTANCE = new SameBlockPredicate();
    public static final MapCodec<SameBlockPredicate> CODEC = MapCodec.unit(INSTANCE);

    private SameBlockPredicate() { }

    @Override
    protected boolean compare(BlockState state, BlockState adjState)
    {
        return state.getBlock() == adjState.getBlock();
    }

    @Override
    public MapCodec<SameBlockPredicate> codec()
    {
        return CODEC;
    }
}
