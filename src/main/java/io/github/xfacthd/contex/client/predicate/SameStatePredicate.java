package io.github.xfacthd.contex.client.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.type.SimpleConnectionPredicate;

public final class SameStatePredicate extends SimpleConnectionPredicate
{
    public static final SameStatePredicate INSTANCE = new SameStatePredicate();
    public static final MapCodec<SameStatePredicate> CODEC = MapCodec.unit(INSTANCE);

    private SameStatePredicate() { }

    @Override
    protected boolean compare(BlockState state, BlockState adjState)
    {
        return state == adjState;
    }

    @Override
    public MapCodec<SameStatePredicate> codec()
    {
        return CODEC;
    }
}
