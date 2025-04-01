package xfacthd.contex.client.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.SimpleConnectionPredicate;

public final class MatchBlockPredicate extends SimpleConnectionPredicate
{
    public static final MapCodec<MatchBlockPredicate> CODEC = BuiltInRegistries.BLOCK.byNameCodec()
            .fieldOf("block")
            .xmap(MatchBlockPredicate::new, pred -> pred.block);

    private final Block block;

    public MatchBlockPredicate(Block block)
    {
        this.block = block;
    }

    @Override
    protected boolean compare(BlockState state, BlockState adjState)
    {
        return state.getBlock() == block && adjState.getBlock() == block;
    }

    @Override
    public MapCodec<MatchBlockPredicate> codec()
    {
        return CODEC;
    }
}
