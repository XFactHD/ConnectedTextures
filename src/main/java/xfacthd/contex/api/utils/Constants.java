package xfacthd.contex.api.utils;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelProperty;
import xfacthd.contex.client.data.ConnectionStateContainer;

import java.util.EnumSet;

public final class Constants
{
    public static final String MOD_ID = "contex";
    public static final ResourceLocation DEFAULT_PREDICATE = Utils.rl("same_block");
    public static final EnumSet<Direction> DIRECTIONS = EnumSet.allOf(Direction.class);
    public static final EnumSet<Direction> HORIZONTAL_DIRECTIONS = EnumSet.range(Direction.NORTH, Direction.EAST);
    public static final ModelProperty<ConnectionStateContainer> CT_STATE_PROPERTY = new ModelProperty<>();



    private Constants() { }
}
