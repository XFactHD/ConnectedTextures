package io.github.xfacthd.contex.api.utils;

import net.minecraft.core.Direction;

import java.util.EnumSet;

public final class Constants {
    public static final String MOD_ID = "contex";
    public static final EnumSet<Direction> DIRECTIONS = EnumSet.allOf(Direction.class);
    public static final EnumSet<Direction> HORIZONTAL_DIRECTIONS = EnumSet.range(Direction.NORTH, Direction.EAST);

    private Constants() { }
}
