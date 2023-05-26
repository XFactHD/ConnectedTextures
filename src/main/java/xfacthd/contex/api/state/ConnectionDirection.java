package xfacthd.contex.api.state;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import xfacthd.contex.api.utils.Constants;

public enum ConnectionDirection
{
    UP(0),
    UP_RIGHT(-1),
    RIGHT(1),
    DOWN_RIGHT(-1),
    DOWN(2),
    DOWN_LEFT(-1),
    LEFT(3),
    UP_LEFT(-1);

    private static final ConnectionDirection[][] DIRECTIONS = makeDirectionsTable();
    private static final ConnectionDirection[][] DIAGONALS = makeDiagonalsTable();
    private static final Vec3i[][] OFFSETS = makeOffsetsTable();

    private final int cardinalIdx;

    ConnectionDirection(int cardinalIdx)
    {
        this.cardinalIdx = cardinalIdx;
    }

    public Vec3i getOffset(Direction side)
    {
        return OFFSETS[ordinal()][side.ordinal()];
    }

    public ConnectionDirection getOpposite()
    {
        return switch (this)
        {
            case UP -> DOWN;
            case UP_RIGHT -> DOWN_LEFT;
            case RIGHT -> LEFT;
            case DOWN_RIGHT -> UP_LEFT;
            case DOWN -> UP;
            case DOWN_LEFT -> UP_RIGHT;
            case LEFT -> RIGHT;
            case UP_LEFT -> DOWN_RIGHT;
        };
    }

    public static ConnectionDirection from(Direction side, Direction dir)
    {
        ConnectionDirection conDir = DIRECTIONS[side.ordinal()][dir.ordinal()];
        if (conDir == null)
        {
            throw new IllegalArgumentException("Invalid side-dir combination: side=" + side + ", dir=" + dir);
        }
        return conDir;
    }

    public static ConnectionDirection diagonal(ConnectionDirection xDir, ConnectionDirection yDir)
    {
        return DIAGONALS[xDir.cardinalIdx][yDir.cardinalIdx];
    }



    private static ConnectionDirection[][] makeDirectionsTable()
    {
        ConnectionDirection[][] directions = new ConnectionDirection[6][6];

        directions[Direction.UP.ordinal()][Direction.NORTH.ordinal()] = DOWN;
        directions[Direction.UP.ordinal()][Direction.EAST.ordinal()] = RIGHT;
        directions[Direction.UP.ordinal()][Direction.SOUTH.ordinal()] = UP;
        directions[Direction.UP.ordinal()][Direction.WEST.ordinal()] = LEFT;

        directions[Direction.DOWN.ordinal()][Direction.NORTH.ordinal()] = UP;
        directions[Direction.DOWN.ordinal()][Direction.EAST.ordinal()] = RIGHT;
        directions[Direction.DOWN.ordinal()][Direction.SOUTH.ordinal()] = DOWN;
        directions[Direction.DOWN.ordinal()][Direction.WEST.ordinal()] = LEFT;

        directions[Direction.NORTH.ordinal()][Direction.UP.ordinal()] = UP;
        directions[Direction.NORTH.ordinal()][Direction.EAST.ordinal()] = RIGHT;
        directions[Direction.NORTH.ordinal()][Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.NORTH.ordinal()][Direction.WEST.ordinal()] = LEFT;

        directions[Direction.SOUTH.ordinal()][Direction.UP.ordinal()] = UP;
        directions[Direction.SOUTH.ordinal()][Direction.EAST.ordinal()] = LEFT;
        directions[Direction.SOUTH.ordinal()][Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.SOUTH.ordinal()][Direction.WEST.ordinal()] = RIGHT;

        directions[Direction.EAST.ordinal()][Direction.UP.ordinal()] = UP;
        directions[Direction.EAST.ordinal()][Direction.NORTH.ordinal()] = LEFT;
        directions[Direction.EAST.ordinal()][Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.EAST.ordinal()][Direction.SOUTH.ordinal()] = RIGHT;

        directions[Direction.WEST.ordinal()][Direction.UP.ordinal()] = UP;
        directions[Direction.WEST.ordinal()][Direction.NORTH.ordinal()] = RIGHT;
        directions[Direction.WEST.ordinal()][Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.WEST.ordinal()][Direction.SOUTH.ordinal()] = LEFT;

        return directions;
    }

    private static ConnectionDirection[][] makeDiagonalsTable()
    {
        ConnectionDirection[][] diagonals = new ConnectionDirection[4][4];
        diagonals[LEFT.cardinalIdx][UP.cardinalIdx] = UP_LEFT;
        diagonals[RIGHT.cardinalIdx][UP.cardinalIdx] = UP_RIGHT;
        diagonals[LEFT.cardinalIdx][DOWN.cardinalIdx] = DOWN_LEFT;
        diagonals[RIGHT.cardinalIdx][DOWN.cardinalIdx] = DOWN_RIGHT;
        return diagonals;
    }

    private static Vec3i[][] makeOffsetsTable()
    {
        ConnectionDirection[] values = values();
        Vec3i[][] offsets = new Vec3i[values.length][];
        for (ConnectionDirection conDir : values)
        {
            Vec3i[] cdOffsets = offsets[conDir.ordinal()] = new Vec3i[Constants.DIRECTIONS.size()];
            for (Direction side : Constants.DIRECTIONS)
            {
                cdOffsets[side.ordinal()] = switch (conDir)
                {
                    case UP -> switch (side)
                    {
                        case DOWN -> new Vec3i(0, 0, -1);
                        case UP -> new Vec3i(0, 0, 1);
                        case NORTH, SOUTH, WEST, EAST -> new Vec3i(0, 1, 0);
                    };
                    case UP_RIGHT -> switch (side)
                    {
                        case DOWN -> new Vec3i(1, 0, -1);
                        case UP -> new Vec3i(1, 0, 1);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getNormal().above();
                    };
                    case RIGHT -> switch (side)
                    {
                        case DOWN, UP -> new Vec3i(1, 0, 0);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getNormal();
                    };
                    case DOWN_RIGHT -> switch (side)
                    {
                        case DOWN -> new Vec3i(1, 0, 1);
                        case UP -> new Vec3i(1, 0, -1);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getNormal().below();
                    };
                    case DOWN -> switch (side)
                    {
                        case DOWN -> new Vec3i(0, 0, 1);
                        case UP -> new Vec3i(0, 0, -1);
                        case NORTH, SOUTH, WEST, EAST -> new Vec3i(0, -1, 0);
                    };
                    case DOWN_LEFT -> switch (side)
                    {
                        case DOWN -> new Vec3i(-1, 0, 1);
                        case UP -> new Vec3i(-1, 0, -1);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getNormal().below();
                    };
                    case LEFT -> switch (side)
                    {
                        case DOWN, UP -> new Vec3i(-1, 0, 0);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getNormal();
                    };
                    case UP_LEFT -> switch (side)
                    {
                        case DOWN -> new Vec3i(-1, 0, -1);
                        case UP -> new Vec3i(-1, 0, 1);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getNormal().above();
                    };
                };
            }
        }
        return offsets;
    }
}
