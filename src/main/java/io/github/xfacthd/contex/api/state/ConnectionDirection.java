package io.github.xfacthd.contex.api.state;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.jspecify.annotations.Nullable;

public enum ConnectionDirection {
    UP(0),
    UP_RIGHT(-1),
    RIGHT(1),
    DOWN_RIGHT(-1),
    DOWN(2),
    DOWN_LEFT(-1),
    LEFT(3),
    UP_LEFT(-1);

    private static final ConnectionDirection[] VALUES = values();
    private static final int CON_DIR_COUNT = VALUES.length;
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DIR_COUNT = DIRECTIONS.length;
    private static final @Nullable ConnectionDirection[] BY_DIRECTION = makeByDirectionTable();
    private static final @Nullable ConnectionDirection[] DIAGONALS = makeDiagonalsTable();
    private static final ConnectionDirection[] OPPOSITES = makeOppositesTable();
    private static final Vec3i[] OFFSETS = makeOffsetsTable();
    private static final Direction[] CUT_EDGES = makeCutEdgesTable();

    private final int cardinalIdx;

    ConnectionDirection(int cardinalIdx) {
        this.cardinalIdx = cardinalIdx;
    }

    public Vec3i getOffset(Direction side) {
        return OFFSETS[ordinal() * DIR_COUNT + side.ordinal()];
    }

    public ConnectionDirection getOpposite() {
        return switch (this) {
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
    public ConnectionDirection mapToOppositeFace(Direction face) {
        return OPPOSITES[this.ordinal() * DIR_COUNT + face.ordinal()];
    }

    /**
     * Check whether this {@link ConnectionDirection} is set on the given connection state
     */
    public boolean isSet(byte connections) {
        return (connections & (1 << ordinal())) != 0;
    }

    /**
     * Set this {@link ConnectionDirection} on the given connection state
     */
    public byte set(byte connections) {
        return (byte) (connections | (byte) (1 << ordinal()));
    }

    /**
     * Check whether both cardinal neighbors of this diagonal {@link ConnectionDirection} are set on the given connection state
     */
    public boolean areCardinalNeighborsSet(byte connections) {
        return switch (this) {
            case UP_RIGHT -> UP.isSet(connections) && RIGHT.isSet(connections);
            case DOWN_RIGHT -> DOWN.isSet(connections) && RIGHT.isSet(connections);
            case DOWN_LEFT -> DOWN.isSet(connections) && LEFT.isSet(connections);
            case UP_LEFT -> UP.isSet(connections) && LEFT.isSet(connections);
            default -> throw new IllegalStateException("Cannot check cardinal neighbors of cardinal direction " + this);
        };
    }

    public boolean isDiagonal() {
        return cardinalIdx == -1;
    }

    public Direction toCutEdge(Direction quadDir) {
        if (isDiagonal()) {
            throw new IllegalArgumentException("Cannot get cutting direction of diagonal ConnectionDirection");
        }
        return CUT_EDGES[quadDir.ordinal() << 2 | cardinalIdx];
    }

    public static ConnectionDirection from(Direction side, Direction dir) {
        ConnectionDirection conDir = BY_DIRECTION[side.ordinal() * DIR_COUNT + dir.ordinal()];
        if (conDir == null) {
            throw new IllegalArgumentException("Invalid side-dir combination: side=" + side + ", dir=" + dir);
        }
        return conDir;
    }

    public static ConnectionDirection diagonal(ConnectionDirection xDir, ConnectionDirection yDir) {
        ConnectionDirection conDir = DIAGONALS[xDir.cardinalIdx * CON_DIR_COUNT + yDir.cardinalIdx];
        if (conDir == null) {
            throw new IllegalArgumentException("Invalid xDir-yDir combination: xDir=" + xDir + ", yDir=" + yDir);
        }
        return conDir;
    }

    public static int mask(ConnectionDirection... directions) {
        byte value = 0;
        for (ConnectionDirection dir : directions) {
            value = dir.set(value);
        }
        return value & 0xFF;
    }


    @SuppressWarnings("ConstantValue")
    private static ConnectionDirection[] makeByDirectionTable() {
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

    private static ConnectionDirection[] makeDiagonalsTable() {
        ConnectionDirection[] diagonals = new ConnectionDirection[CON_DIR_COUNT * CON_DIR_COUNT];
        diagonals[LEFT.cardinalIdx * CON_DIR_COUNT + UP.cardinalIdx] = UP_LEFT;
        diagonals[RIGHT.cardinalIdx * CON_DIR_COUNT + UP.cardinalIdx] = UP_RIGHT;
        diagonals[LEFT.cardinalIdx * CON_DIR_COUNT + DOWN.cardinalIdx] = DOWN_LEFT;
        diagonals[RIGHT.cardinalIdx * CON_DIR_COUNT + DOWN.cardinalIdx] = DOWN_RIGHT;
        return diagonals;
    }

    @SuppressWarnings("ConstantValue")
    private static ConnectionDirection[] makeOppositesTable() {
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

    private static Vec3i[] makeOffsetsTable() {
        Vec3i[] offsets = new Vec3i[VALUES.length * DIR_COUNT];
        for (ConnectionDirection conDir : VALUES) {
            int baseIdx = conDir.ordinal() * DIR_COUNT;
            for (Direction side : DIRECTIONS) {
                offsets[baseIdx + side.ordinal()] = switch (conDir) {
                    case UP -> switch (side) {
                        case DOWN -> new Vec3i(0, 0, -1);
                        case UP -> new Vec3i(0, 0, 1);
                        case NORTH, SOUTH, WEST, EAST -> new Vec3i(0, 1, 0);
                    };
                    case UP_RIGHT -> switch (side) {
                        case DOWN -> new Vec3i(1, 0, -1);
                        case UP -> new Vec3i(1, 0, 1);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getUnitVec3i().above();
                    };
                    case RIGHT -> switch (side) {
                        case DOWN, UP -> new Vec3i(1, 0, 0);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getUnitVec3i();
                    };
                    case DOWN_RIGHT -> switch (side) {
                        case DOWN -> new Vec3i(1, 0, 1);
                        case UP -> new Vec3i(1, 0, -1);
                        case NORTH, SOUTH, WEST, EAST -> side.getClockWise().getUnitVec3i().below();
                    };
                    case DOWN -> switch (side) {
                        case DOWN -> new Vec3i(0, 0, 1);
                        case UP -> new Vec3i(0, 0, -1);
                        case NORTH, SOUTH, WEST, EAST -> new Vec3i(0, -1, 0);
                    };
                    case DOWN_LEFT -> switch (side) {
                        case DOWN -> new Vec3i(-1, 0, 1);
                        case UP -> new Vec3i(-1, 0, -1);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getUnitVec3i().below();
                    };
                    case LEFT -> switch (side) {
                        case DOWN, UP -> new Vec3i(-1, 0, 0);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getUnitVec3i();
                    };
                    case UP_LEFT -> switch (side) {
                        case DOWN -> new Vec3i(-1, 0, -1);
                        case UP -> new Vec3i(-1, 0, 1);
                        case NORTH, SOUTH, WEST, EAST -> side.getCounterClockWise().getUnitVec3i().above();
                    };
                };
            }
        }
        return offsets;
    }

    private static Direction[] makeCutEdgesTable() {
        Direction[] directions = new Direction[24];
        for (Direction side : DIRECTIONS) {
            for (ConnectionDirection conDir : VALUES) {
                if (conDir.isDiagonal()) {
                    continue;
                }

                directions[side.ordinal() << 2 | conDir.cardinalIdx] = switch (conDir) {
                    case UP -> switch (side) {
                        case DOWN -> Direction.SOUTH;
                        case UP -> Direction.NORTH;
                        default -> Direction.DOWN;
                    };
                    case DOWN -> switch (side) {
                        case DOWN -> Direction.NORTH;
                        case UP -> Direction.SOUTH;
                        default -> Direction.UP;
                    };
                    case LEFT -> switch (side) {
                        case DOWN, UP -> Direction.EAST;
                        default -> side.getClockWise();
                    };
                    case RIGHT -> switch (side) {
                        case DOWN, UP -> Direction.WEST;
                        default -> side.getCounterClockWise();
                    };
                    default -> throw new IllegalStateException();
                };
            }
        }
        return directions;
    }
}
