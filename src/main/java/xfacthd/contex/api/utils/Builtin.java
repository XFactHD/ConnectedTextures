package xfacthd.contex.api.utils;

import net.minecraft.resources.ResourceLocation;

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



        private Types() { }
    }

    public static final class Predicates
    {
        public static final ResourceLocation SAME_BLOCK = Utils.rl("same_block");
        public static final ResourceLocation SAME_STATE = Utils.rl("same_state");



        private Predicates() { }
    }



    private Builtin() { }
}
