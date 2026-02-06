package io.github.xfacthd.contex.api.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

record CuttingConfig(
        int forwardCoord,
        VertPair cutEdgeVerts,
        VertPair checkEdgeVerts,
        UvSrcVertSet uvVertsOne,
        UvSrcVertSet uvVertsTwo,
        boolean vAxis
) {
    record VertPair(int v1, int v2) { }

    record UvSrcVertSet(int posOne, int posTwo, int uvOne, int uvTwo) { }

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final @Nullable CuttingConfig[] CUTTING_CONFIGS = computeCuttingConfigs();

    static CuttingConfig get(Direction quadDir, Direction cutEdge)
    {
        CuttingConfig config = CUTTING_CONFIGS[makeConfigIndex(quadDir, cutEdge)];
        if (config == null)
        {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Invalid direction pair (quadDir: %s, cutEdge: %s)", quadDir, cutEdge));
        }
        return config;
    }

    private static CuttingConfig[] computeCuttingConfigs()
    {
        CuttingConfig[] arr = new CuttingConfig[6 * 8];
        for (Direction quadDir : DIRECTIONS)
        {
            for (Direction cutEdge : DIRECTIONS)
            {
                if (quadDir.getAxis() != cutEdge.getAxis())
                {
                    arr[makeConfigIndex(quadDir, cutEdge)] = computeCuttingConfig(quadDir, cutEdge);
                }
            }
        }
        return arr;
    }

    private static int makeConfigIndex(Direction quadDir, Direction cutEdge)
    {
        return quadDir.ordinal() << 3 | cutEdge.ordinal();
    }

    private static CuttingConfig computeCuttingConfig(Direction quadDir, Direction cutEdge)
    {
        Direction.Axis quadAxis = quadDir.getAxis();
        Direction.Axis cutAxis = cutEdge.getAxis();
        CuttingConfig.VertPair cutEdgeVerts = getCutEdgeVertPair(quadDir, cutEdge);
        Pair<UvSrcVertSet, UvSrcVertSet> uvVerts = getUvVerts(quadAxis, cutAxis);
        return new CuttingConfig(
                getForwardCoord(quadAxis, cutAxis),
                cutEdgeVerts,
                getCheckEdgeVertPair(quadDir, cutEdge),
                uvVerts.getFirst(),
                uvVerts.getSecond(),
                isVAxis(quadAxis, cutAxis)
        );
    }

    /**
     * {@return the coordinate axis index along the axis of the cutting direction}
     */
    private static int getForwardCoord(Direction.Axis quadAxis, Direction.Axis cutAxis)
    {
        return switch (quadAxis)
        {
            case X -> switch (cutAxis)
            {
                case Y -> 1;
                case Z -> 2;
                case X -> throw new IllegalArgumentException();
            };
            case Y -> switch (cutAxis)
            {
                case X -> 0;
                case Z -> 2;
                case Y -> throw new IllegalArgumentException();
            };
            case Z -> switch (cutAxis)
            {
                case X -> 0;
                case Y -> 1;
                case Z -> throw new IllegalArgumentException();
            };
        };
    }

    /**
     * {@return the vertex pair at the cutting edge which represents the vertices that get moved by the cutting operation}
     */
    private static VertPair getCutEdgeVertPair(Direction quadDir, Direction cutEdge)
    {
        return switch (quadDir)
        {
            case DOWN -> switch (cutEdge)
            {
                case NORTH -> new VertPair(1, 2);
                case SOUTH -> new VertPair(0, 3);
                case WEST -> new VertPair(1, 0);
                case EAST -> new VertPair(2, 3);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case UP -> switch (cutEdge)
            {
                case NORTH -> new VertPair(0, 3);
                case SOUTH -> new VertPair(1, 2);
                case WEST -> new VertPair(1, 0);
                case EAST -> new VertPair(2, 3);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case NORTH -> switch (cutEdge)
            {
                case DOWN -> new VertPair(1, 2);
                case UP -> new VertPair(0, 3);
                case WEST -> new VertPair(3, 2);
                case EAST -> new VertPair(0, 1);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case SOUTH -> switch (cutEdge)
            {
                case DOWN -> new VertPair(1, 2);
                case UP -> new VertPair(0, 3);
                case WEST -> new VertPair(0, 1);
                case EAST -> new VertPair(3, 2);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case WEST -> switch (cutEdge)
            {
                case DOWN -> new VertPair(1, 2);
                case UP -> new VertPair(0, 3);
                case NORTH -> new VertPair(0, 1);
                case SOUTH -> new VertPair(3, 2);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
            case EAST -> switch (cutEdge)
            {
                case DOWN -> new VertPair(1, 2);
                case UP -> new VertPair(0, 3);
                case NORTH -> new VertPair(3, 2);
                case SOUTH -> new VertPair(0, 1);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
        };
    }

    /**
     * {@return the vertex pair opposite the cutting edge which represents the vertices against which the cutting target is checked}
     */
    private static VertPair getCheckEdgeVertPair(Direction quadDir, Direction cutEdge)
    {
        return switch (quadDir)
        {
            case DOWN -> switch (cutEdge)
            {
                case NORTH -> new VertPair(0, 3);
                case SOUTH -> new VertPair(1, 2);
                case WEST -> new VertPair(2, 3);
                case EAST -> new VertPair(1, 0);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case UP -> switch (cutEdge)
            {
                case NORTH -> new VertPair(1, 2);
                case SOUTH -> new VertPair(0, 3);
                case WEST -> new VertPair(2, 3);
                case EAST -> new VertPair(1, 0);
                case DOWN, UP -> throw new IllegalArgumentException();
            };
            case NORTH -> switch (cutEdge)
            {
                case DOWN -> new VertPair(0, 3);
                case UP -> new VertPair(1, 2);
                case WEST -> new VertPair(0, 1);
                case EAST -> new VertPair(3, 2);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case SOUTH -> switch (cutEdge)
            {
                case DOWN -> new VertPair(0, 3);
                case UP -> new VertPair(1, 2);
                case WEST -> new VertPair(3, 2);
                case EAST -> new VertPair(0, 1);
                case NORTH, SOUTH -> throw new IllegalArgumentException();
            };
            case WEST -> switch (cutEdge)
            {
                case DOWN -> new VertPair(0, 3);
                case UP -> new VertPair(1, 2);
                case NORTH -> new VertPair(3, 2);
                case SOUTH -> new VertPair(0, 1);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
            case EAST -> switch (cutEdge)
            {
                case DOWN -> new VertPair(0, 3);
                case UP -> new VertPair(1, 2);
                case NORTH -> new VertPair(0, 1);
                case SOUTH -> new VertPair(3, 2);
                case WEST, EAST -> throw new IllegalArgumentException();
            };
        };
    }

    private static Pair<UvSrcVertSet, UvSrcVertSet> getUvVerts(Direction.Axis quadAxis, Direction.Axis cutAxis)
    {
        if (quadAxis == Direction.Axis.Y)
        {
            return switch (cutAxis)
            {
                case X -> Pair.of(
                        new UvSrcVertSet(1, 2, 1, 2),
                        new UvSrcVertSet(0, 3, 0, 3)
                );
                case Z -> Pair.of(
                        new UvSrcVertSet(1, 0, 0, 1),
                        new UvSrcVertSet(2, 3, 3, 2)
                );
                case Y -> throw new IllegalArgumentException();
            };
        }
        if (cutAxis == Direction.Axis.Y)
        {
            return Pair.of(
                    new UvSrcVertSet(1, 0, 0, 1),
                    new UvSrcVertSet(2, 3, 3, 2)
            );
        }
        return Pair.of(
                new UvSrcVertSet(0, 3, 0, 3),
                new UvSrcVertSet(1, 2, 1, 2)
        );
    }

    private static boolean isVAxis(Direction.Axis quadAxis, Direction.Axis cutAxis)
    {
        if (quadAxis == Direction.Axis.Y)
        {
            return cutAxis == Direction.Axis.Z;
        }
        return cutAxis == Direction.Axis.Y;
    }
}
