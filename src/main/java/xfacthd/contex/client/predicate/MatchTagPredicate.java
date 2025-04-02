package xfacthd.contex.client.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.SimpleConnectionPredicate;

public final class MatchTagPredicate extends SimpleConnectionPredicate
{
    private static final Codec<TagKey<Block>> TAG_CODEC = TagKey.codec(Registries.BLOCK);
    public static final MapCodec<MatchTagPredicate> CODEC = Codec.mapEither(
            TAG_CODEC.fieldOf("tag"),
            Codec.mapPair(TAG_CODEC.fieldOf("self_tag"), TAG_CODEC.fieldOf("other_tag"))
    ).xmap(MatchTagPredicate::ofEither, MatchTagPredicate::toEither);

    private final TagKey<Block> selfTag;
    private final TagKey<Block> otherTag;

    public MatchTagPredicate(TagKey<Block> selfTag, TagKey<Block> otherTag)
    {
        this.selfTag = selfTag;
        this.otherTag = otherTag;
    }

    @Override
    protected boolean compare(BlockState state, BlockState adjState)
    {
        return state.is(selfTag) && adjState.is(otherTag);
    }

    private Either<TagKey<Block>, Pair<TagKey<Block>, TagKey<Block>>> toEither()
    {
        return selfTag == otherTag ? Either.left(selfTag) : Either.right(Pair.of(selfTag, otherTag));
    }

    @Override
    public MapCodec<? extends ConnectionPredicate> codec()
    {
        return CODEC;
    }

    private static MatchTagPredicate ofEither(Either<TagKey<Block>, Pair<TagKey<Block>, TagKey<Block>>> either)
    {
        return either.map(
                tag -> new MatchTagPredicate(tag, tag),
                pair -> new MatchTagPredicate(pair.getFirst(), pair.getSecond())
        );
    }
}
