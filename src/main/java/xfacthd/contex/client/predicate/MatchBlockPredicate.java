package xfacthd.contex.client.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.SimpleConnectionPredicate;

public final class MatchBlockPredicate extends SimpleConnectionPredicate
{
    public static final MapCodec<MatchBlockPredicate> CODEC = Codec.mapEither(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block"),
            Codec.mapPair(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("self_block"), BuiltInRegistries.BLOCK.byNameCodec().fieldOf("other_block"))
    ).xmap(MatchBlockPredicate::ofEither, MatchBlockPredicate::toEither);

    private final Block selfBlock;
    private final Block otherBlock;

    public MatchBlockPredicate(Block selfBlock, Block otherBlock)
    {
        this.selfBlock = selfBlock;
        this.otherBlock = otherBlock;
    }

    @Override
    protected boolean compare(BlockState state, BlockState adjState)
    {
        return state.getBlock() == selfBlock && adjState.getBlock() == otherBlock;
    }

    private Either<Block, Pair<Block, Block>> toEither()
    {
        return selfBlock == otherBlock ? Either.left(selfBlock) : Either.right(Pair.of(selfBlock, otherBlock));
    }

    @Override
    public MapCodec<MatchBlockPredicate> codec()
    {
        return CODEC;
    }

    private static MatchBlockPredicate ofEither(Either<Block, Pair<Block, Block>> either)
    {
        return either.map(
                state -> new MatchBlockPredicate(state, state),
                pair -> new MatchBlockPredicate(pair.getFirst(), pair.getSecond())
        );
    }
}
