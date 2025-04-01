package xfacthd.contex.api.utils;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.TextureType;
import xfacthd.contex.client.predicate.MatchBlockPredicate;
import xfacthd.contex.client.predicate.MatchStatePredicate;
import xfacthd.contex.client.predicate.SameBlockPredicate;
import xfacthd.contex.client.predicate.SameStatePredicate;
import xfacthd.contex.client.type.FullCarpetTextureType;
import xfacthd.contex.client.type.FullTextureType;
import xfacthd.contex.client.type.OmniPillarTextureType;
import xfacthd.contex.client.type.PillarTextureType;
import xfacthd.contex.client.type.SimpleCarpetTextureType;
import xfacthd.contex.client.type.SimpleTextureType;

public final class Builtin
{
    public static final class Types
    {
        public static TextureType simple()
        {
            return SimpleTextureType.INSTANCE;
        }

        public static TextureType full()
        {
            return FullTextureType.INSTANCE;
        }

        public static TextureType pillar(Direction.Axis axis)
        {
            return switch (axis)
            {
                case X -> PillarTextureType.X;
                case Y -> PillarTextureType.Y;
                case Z -> PillarTextureType.Z;
            };
        }

        public static TextureType pillarOmni()
        {
            return OmniPillarTextureType.INSTANCE;
        }

        public static TextureType carpetSimple(Direction.Axis axis)
        {
            return switch (axis)
            {
                case X -> SimpleCarpetTextureType.X;
                case Y -> SimpleCarpetTextureType.Y;
                case Z -> SimpleCarpetTextureType.Z;
            };
        }

        public static TextureType carpetFull(Direction.Axis axis)
        {
            return switch (axis)
            {
                case X -> FullCarpetTextureType.X;
                case Y -> FullCarpetTextureType.Y;
                case Z -> FullCarpetTextureType.Z;
            };
        }

        private Types() { }
    }

    public static final class Predicates
    {
        public static ConnectionPredicate sameBlock()
        {
            return SameBlockPredicate.INSTANCE;
        }

        public static ConnectionPredicate sameState()
        {
            return SameStatePredicate.INSTANCE;
        }

        public static ConnectionPredicate matchBlock(Block block)
        {
            return new MatchBlockPredicate(block);
        }

        public static ConnectionPredicate matchState(BlockState state)
        {
            return new MatchStatePredicate(state);
        }

        private Predicates() { }
    }

    private Builtin() { }
}
