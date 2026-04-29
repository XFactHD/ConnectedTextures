package io.github.xfacthd.contex.api.utils;

import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.client.predicate.MatchBlockPredicate;
import io.github.xfacthd.contex.client.predicate.MatchStatePredicate;
import io.github.xfacthd.contex.client.predicate.MatchTagPredicate;
import io.github.xfacthd.contex.client.predicate.SameBlockPredicate;
import io.github.xfacthd.contex.client.predicate.SameStatePredicate;
import io.github.xfacthd.contex.client.strategy.CompactTextureStrategy;
import io.github.xfacthd.contex.client.strategy.FullTextureStrategy;
import io.github.xfacthd.contex.client.type.FullCarpetTextureType;
import io.github.xfacthd.contex.client.type.FullTextureType;
import io.github.xfacthd.contex.client.type.OmniPillarTextureType;
import io.github.xfacthd.contex.client.type.PillarTextureType;
import io.github.xfacthd.contex.client.type.RotatingPillarTextureType;
import io.github.xfacthd.contex.client.type.SimpleCarpetTextureType;
import io.github.xfacthd.contex.client.type.SimpleTextureType;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public final class Builtin {
    public static final class Types {
        public static TextureType simple() {
            return SimpleTextureType.INSTANCE;
        }

        public static TextureType full() {
            return FullTextureType.INSTANCE;
        }

        public static TextureType pillar(Direction.Axis axis) {
            return switch (axis) {
                case X -> PillarTextureType.X;
                case Y -> PillarTextureType.Y;
                case Z -> PillarTextureType.Z;
            };
        }

        public static TextureType pillarRotating(Direction.Axis axis) {
            return switch (axis) {
                case X -> RotatingPillarTextureType.X;
                case Y -> RotatingPillarTextureType.Y;
                case Z -> RotatingPillarTextureType.Z;
            };
        }

        public static TextureType pillarOmni() {
            return OmniPillarTextureType.INSTANCE;
        }

        public static TextureType carpetSimple(Direction dir) {
            return SimpleCarpetTextureType.TYPES[dir.ordinal()];
        }

        public static TextureType carpetFull(Direction dir) {
            return FullCarpetTextureType.TYPES[dir.ordinal()];
        }

        private Types() { }
    }

    public static final class Predicates {
        public static ConnectionPredicate sameBlock() {
            return SameBlockPredicate.INSTANCE;
        }

        public static ConnectionPredicate sameState() {
            return SameStatePredicate.INSTANCE;
        }

        public static ConnectionPredicate matchBlock(Block block) {
            return matchBlock(block, block);
        }

        public static ConnectionPredicate matchBlock(Block selfBlock, Block otherBlock) {
            return new MatchBlockPredicate(selfBlock, otherBlock);
        }

        public static ConnectionPredicate matchState(BlockState state) {
            return matchState(state, state);
        }

        public static ConnectionPredicate matchState(BlockState selfState, BlockState otherState) {
            return new MatchStatePredicate(selfState, otherState);
        }

        public static ConnectionPredicate matchTag(TagKey<Block> tag) {
            return matchTag(tag, tag);
        }

        public static ConnectionPredicate matchTag(TagKey<Block> selfTag, TagKey<Block> otherTag) {
            return new MatchTagPredicate(selfTag, otherTag);
        }

        private Predicates() { }
    }

    public static final class Strategies {
        public static TextureStrategy compact() {
            return CompactTextureStrategy.INSTANCE;
        }

        public static TextureStrategy full() {
            return FullTextureStrategy.INSTANCE;
        }

        private Strategies() { }
    }

    private Builtin() { }
}
