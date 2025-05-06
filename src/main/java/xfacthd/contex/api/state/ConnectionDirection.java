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

    private static final int CON_DIR_COUNT = values().length;
    private static final int DIR_COUNT = Direction.values().length;
    private static final ConnectionDirection[] DIRECTIONS = makeDirectionsTable();
    private static final ConnectionDirection[] DIAGONALS = makeDiagonalsTable();
    private static final ConnectionDirection[] OPPOSITES = makeOppositesTable();
    private static final Vec3i[] OFFSETS = makeOffsetsTable();

    private final int cardinalIdx;

    ConnectionDirection(int cardinalIdx)
    {
        this.cardinalIdx = cardinalIdx;
    }

    public Vec3i getOffset(Direction side)
    {
        return OFFSETS[ordinal() * DIR_COUNT + side.ordinal()];
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

    /**
     * {@return the {@link ConnectionDirection} pointing in the same absolute direction for the opposite of the given face}
     */
    public ConnectionDirection mapToOppositeFace(Direction face)
    {
        return OPPOSITES[this.ordinal() * DIR_COUNT + face.ordinal()];
    }

    /**
     * Check whether this {@link ConnectionDirection} is set on the given connection state
     */
    public boolean isSet(byte connections)
    {
        return (connections & (1 << ordinal())) != 0;
    }

    /**
     * Set this {@link ConnectionDirection} on the given connection state
     */
    public byte set(byte connections)
    {
        return (byte) (connections | (byte) (1 << ordinal()));
    }

    public static ConnectionDirection from(Direction side, Direction dir)
    {
        ConnectionDirection conDir = DIRECTIONS[side.ordinal() * DIR_COUNT + dir.ordinal()];
        if (conDir == null)
        {
            throw new IllegalArgumentException("Invalid side-dir combination: side=" + side + ", dir=" + dir);
        }
        return conDir;
    }

    public static ConnectionDirection diagonal(ConnectionDirection xDir, ConnectionDirection yDir)
    {
        ConnectionDirection conDir = DIAGONALS[xDir.cardinalIdx * CON_DIR_COUNT + yDir.cardinalIdx];
        if (conDir == null)
        {
            throw new IllegalArgumentException("Invalid xDir-yDir combination: xDir=" + xDir + ", yDir=" + yDir);
        }
        return conDir;
    }



    @SuppressWarnings("ConstantValue")
    private static ConnectionDirection[] makeDirectionsTable()
    {
        ConnectionDirection[] directions = new ConnectionDirection[DIR_COUNT * DIR_COUNT];

        directions[Direction.UP.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = DOWN;
        directions[Direction.UP.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = RIGHT;
        directions[Direction.UP.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = UP;
        directions[Direction.UP.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = LEFT;

        directions[Direction.DOWN.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = UP;
        directions[Direction.DOWN.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = RIGHT;
        directions[Direction.DOWN.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = DOWN;
        directions[Direction.DOWN.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = LEFT;

        directions[Direction.NORTH.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = UP;
        directions[Direction.NORTH.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = RIGHT;
        directions[Direction.NORTH.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.NORTH.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = LEFT;

        directions[Direction.SOUTH.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = UP;
        directions[Direction.SOUTH.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = LEFT;
        directions[Direction.SOUTH.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.SOUTH.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = RIGHT;

        directions[Direction.EAST.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = UP;
        directions[Direction.EAST.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = LEFT;
        directions[Direction.EAST.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.EAST.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = RIGHT;

        directions[Direction.WEST.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = UP;
        directions[Direction.WEST.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = RIGHT;
        directions[Direction.WEST.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = DOWN;
        directions[Direction.WEST.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = LEFT;

        return directions;
    }

    private static ConnectionDirection[] makeDiagonalsTable()
    {
        ConnectionDirection[] diagonals = new ConnectionDirection[CON_DIR_COUNT * CON_DIR_COUNT];
        diagonals[LEFT.cardinalIdx * CON_DIR_COUNT + UP.cardinalIdx] = UP_LEFT;
        diagonals[RIGHT.cardinalIdx * CON_DIR_COUNT + UP.cardinalIdx] = UP_RIGHT;
        diagonals[LEFT.cardinalIdx * CON_DIR_COUNT + DOWN.cardinalIdx] = DOWN_LEFT;
        diagonals[RIGHT.cardinalIdx * CON_DIR_COUNT + DOWN.cardinalIdx] = DOWN_RIGHT;
        return diagonals;
    }

    @SuppressWarnings("ConstantValue")
    private static ConnectionDirection[] makeOppositesTable()
    {
        ConnectionDirection[] directions = new ConnectionDirection[CON_DIR_COUNT * DIR_COUNT];

        directions[ConnectionDirection.UP.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.DOWN;
        directions[ConnectionDirection.UP.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.DOWN;
        directions[ConnectionDirection.UP.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.UP;
        directions[ConnectionDirection.UP.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.UP;
        directions[ConnectionDirection.UP.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.UP;
        directions[ConnectionDirection.UP.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.UP;

        directions[ConnectionDirection.UP_RIGHT.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.DOWN_RIGHT;
        directions[ConnectionDirection.UP_RIGHT.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.DOWN_RIGHT;
        directions[ConnectionDirection.UP_RIGHT.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.UP_LEFT;
        directions[ConnectionDirection.UP_RIGHT.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.UP_LEFT;
        directions[ConnectionDirection.UP_RIGHT.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.UP_LEFT;
        directions[ConnectionDirection.UP_RIGHT.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.UP_LEFT;

        directions[ConnectionDirection.RIGHT.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.RIGHT;
        directions[ConnectionDirection.RIGHT.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.RIGHT;
        directions[ConnectionDirection.RIGHT.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.LEFT;
        directions[ConnectionDirection.RIGHT.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.LEFT;
        directions[ConnectionDirection.RIGHT.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.LEFT;
        directions[ConnectionDirection.RIGHT.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.LEFT;

        directions[ConnectionDirection.DOWN_RIGHT.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.UP_RIGHT;
        directions[ConnectionDirection.DOWN_RIGHT.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.UP_RIGHT;
        directions[ConnectionDirection.DOWN_RIGHT.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.DOWN_LEFT;
        directions[ConnectionDirection.DOWN_RIGHT.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.DOWN_LEFT;
        directions[ConnectionDirection.DOWN_RIGHT.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.DOWN_LEFT;
        directions[ConnectionDirection.DOWN_RIGHT.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.DOWN_LEFT;

        directions[ConnectionDirection.DOWN.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.UP;
        directions[ConnectionDirection.DOWN.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.UP;
        directions[ConnectionDirection.DOWN.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.DOWN;
        directions[ConnectionDirection.DOWN.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.DOWN;
        directions[ConnectionDirection.DOWN.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.DOWN;
        directions[ConnectionDirection.DOWN.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.DOWN;

        directions[ConnectionDirection.DOWN_LEFT.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.UP_LEFT;
        directions[ConnectionDirection.DOWN_LEFT.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.UP_LEFT;
        directions[ConnectionDirection.DOWN_LEFT.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.DOWN_RIGHT;
        directions[ConnectionDirection.DOWN_LEFT.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.DOWN_RIGHT;
        directions[ConnectionDirection.DOWN_LEFT.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.DOWN_RIGHT;
        directions[ConnectionDirection.DOWN_LEFT.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.DOWN_RIGHT;

        directions[ConnectionDirection.LEFT.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.LEFT;
        directions[ConnectionDirection.LEFT.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.LEFT;
        directions[ConnectionDirection.LEFT.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.RIGHT;
        directions[ConnectionDirection.LEFT.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.RIGHT;
        directions[ConnectionDirection.LEFT.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.RIGHT;
        directions[ConnectionDirection.LEFT.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.RIGHT;

        directions[ConnectionDirection.UP_LEFT.ordinal() * DIR_COUNT + Direction.UP.ordinal()] = ConnectionDirection.DOWN_LEFT;
        directions[ConnectionDirection.UP_LEFT.ordinal() * DIR_COUNT + Direction.DOWN.ordinal()] = ConnectionDirection.DOWN_LEFT;
        directions[ConnectionDirection.UP_LEFT.ordinal() * DIR_COUNT + Direction.NORTH.ordinal()] = ConnectionDirection.UP_RIGHT;
        directions[ConnectionDirection.UP_LEFT.ordinal() * DIR_COUNT + Direction.SOUTH.ordinal()] = ConnectionDirection.UP_RIGHT;
        directions[ConnectionDirection.UP_LEFT.ordinal() * DIR_COUNT + Direction.EAST.ordinal()] = ConnectionDirection.UP_RIGHT;
        directions[ConnectionDirection.UP_LEFT.ordinal() * DIR_COUNT + Direction.WEST.ordinal()] = ConnectionDirection.UP_RIGHT;

        return directions;
    }

    private static Vec3i[] makeOffsetsTable()
    {
        ConnectionDirection[] values = values();
        Vec3i[] offsets = new Vec3i[values.length * DIR_COUNT];
        for (ConnectionDirection conDir : values)
        {
            int baseIdx = conDir.ordinal() * DIR_COUNT;
            for (Direction side : Constants.DIRECTIONS)
            {
                offsets[baseIdx + side.ordinal()] = switch (conDir)
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
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getUnitVec3i().above();
                    };
                    case RIGHT -> switch (side)
                    {
                        case DOWN, UP -> new Vec3i(1, 0, 0);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getUnitVec3i();
                    };
                    case DOWN_RIGHT -> switch (side)
                    {
                        case DOWN -> new Vec3i(1, 0, 1);
                        case UP -> new Vec3i(1, 0, -1);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getUnitVec3i().below();
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
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getUnitVec3i().below();
                    };
                    case LEFT -> switch (side)
                    {
                        case DOWN, UP -> new Vec3i(-1, 0, 0);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getUnitVec3i();
                    };
                    case UP_LEFT -> switch (side)
                    {
                        case DOWN -> new Vec3i(-1, 0, -1);
                        case UP -> new Vec3i(-1, 0, 1);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getUnitVec3i().above();
                    };
                };
            }
        }
        return offsets;
    }
}
