package xfacthd.contex.api.utils;

import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.TextureType;
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
        public static final ResourceLocation SIMPLE = Utils.rl("simple");
        public static final ResourceLocation FULL = Utils.rl("full");
        public static final ResourceLocation PILLAR_X = Utils.rl("pillar_x");
        public static final ResourceLocation PILLAR_Y = Utils.rl("pillar_y");
        public static final ResourceLocation PILLAR_Z = Utils.rl("pillar_z");
        public static final ResourceLocation PILLAR_OMNI = Utils.rl("pillar_omni");
        public static final ResourceLocation CARPET_SIMPLE = Utils.rl("carpet_simple");
        public static final ResourceLocation CARPET_SIMPLE_X = Utils.rl("carpet_simple_x");
        public static final ResourceLocation CARPET_SIMPLE_Z = Utils.rl("carpet_simple_z");
        public static final ResourceLocation CARPET_FULL = Utils.rl("carpet_full");
        public static final ResourceLocation CARPET_FULL_X = Utils.rl("carpet_full_x");
        public static final ResourceLocation CARPET_FULL_Z = Utils.rl("carpet_full_z");

        public static final TextureType SIMPLE_TYPE = SimpleTextureType.INSTANCE;
        public static final TextureType FULL_TYPE = FullTextureType.INSTANCE;
        public static final TextureType PILLAR_X_TYPE = PillarTextureType.X;
        public static final TextureType PILLAR_Y_TYPE = PillarTextureType.Y;
        public static final TextureType PILLAR_Z_TYPE = PillarTextureType.Z;
        public static final TextureType PILLAR_OMNI_TYPE = OmniPillarTextureType.INSTANCE;
        public static final TextureType CARPET_SIMPLE_TYPE = SimpleCarpetTextureType.Y;
        public static final TextureType CARPET_SIMPLE_TYPE_X = SimpleCarpetTextureType.X;
        public static final TextureType CARPET_SIMPLE_TYPE_Z = SimpleCarpetTextureType.Z;
        public static final TextureType CARPET_FULL_TYPE = FullCarpetTextureType.Y;
        public static final TextureType CARPET_FULL_TYPE_X = FullCarpetTextureType.X;
        public static final TextureType CARPET_FULL_TYPE_Z = FullCarpetTextureType.Z;



        private Types() { }
    }

    public static final class Predicates
    {
        public static final ResourceLocation SAME_BLOCK = Utils.rl("same_block");
        public static final ResourceLocation SAME_STATE = Utils.rl("same_state");

        public static final ConnectionPredicate SAME_BLOCK_PRED = SameBlockPredicate.INSTANCE;
        public static final ConnectionPredicate SAME_STATE_PRED = SameStatePredicate.INSTANCE;



        private Predicates() { }
    }



    private Builtin() { }
}
