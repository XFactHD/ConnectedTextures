package io.github.xfacthd.contex.client.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.type.SimpleConnectionPredicate;

public final class MatchStatePredicate extends SimpleConnectionPredicate
{
    public static final MapCodec<MatchStatePredicate> CODEC = Codec.mapEither(
            BlockState.CODEC.fieldOf("state"),
            Codec.mapPair(BlockState.CODEC.fieldOf("self_state"), BlockState.CODEC.fieldOf("other_state"))
    ).xmap(MatchStatePredicate::ofEither, MatchStatePredicate::toEither);

    private final BlockState selfState;
    private final BlockState otherState;

    public MatchStatePredicate(BlockState selfState, BlockState otherState)
    {
        this.selfState = selfState;
        this.otherState = otherState;
    }

    @Override
    protected boolean compare(BlockState state, BlockState adjState)
    {
        return state == selfState && adjState == otherState;
    }

    private Either<BlockState, Pair<BlockState, BlockState>> toEither()
    {
        return selfState == otherState ? Either.left(selfState) : Either.right(Pair.of(selfState, otherState));
    }

    @Override
    public MapCodec<MatchStatePredicate> codec()
    {
        return CODEC;
    }

    private static MatchStatePredicate ofEither(Either<BlockState, Pair<BlockState, BlockState>> either)
    {
        return either.map(
                state -> new MatchStatePredicate(state, state),
                pair -> new MatchStatePredicate(pair.getFirst(), pair.getSecond())
        );
    }
}
