package io.github.xfacthd.contex.client.type;

import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.type.TextureType;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public final class OmniPillarTextureType extends TextureType {
    private static final ConnectionDirection[] DIRECTIONS = ConnectionDirection.values();
    private static final Direction[] DIR_AXIS_Y = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    private static final Direction[] DIR_AXIS_X = new Direction[] { Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH };
    private static final Direction[] DIR_AXIS_Z = new Direction[] { Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST };
    private static final Direction[] DIAG_DIR_AXIS_X = new Direction[] { Direction.NORTH, Direction.SOUTH };
    private static final Direction[] DIAG_DIR_AXIS_Z = new Direction[] { Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN };
    private static final int DIAG_CHECK_MASK = ConnectionDirection.mask(
            ConnectionDirection.UP_LEFT, ConnectionDirection.UP_RIGHT, ConnectionDirection.DOWN_LEFT, ConnectionDirection.DOWN_RIGHT
    );
    private static final int CON_MASK_AXIS_Y = ConnectionDirection.mask(ConnectionDirection.UP, ConnectionDirection.DOWN);
    private static final int CON_MASK_DIAG_AXIS_X = ConnectionDirection.mask(ConnectionDirection.UP, ConnectionDirection.DOWN);
    private static final int CON_MASK_AXIS_X = ConnectionDirection.mask(ConnectionDirection.LEFT, ConnectionDirection.RIGHT);
    private static final int CON_MASK_DIAG_AXIS_Z = 0;
    private static final Set<SpriteType> SPRITE_TYPES = Set.of(SpriteType.HORIZONTAL, SpriteType.VERTICAL);
    public static final OmniPillarTextureType INSTANCE = new OmniPillarTextureType();

    private OmniPillarTextureType() { }

    @Override
    public byte getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    ) {
        byte connections = 0;
        for (ConnectionDirection dir : DIRECTIONS) {
            connections = testDirection(dir, connections, level, pos, state, side, predicate, occlusionMode);
        }
        return connections;
    }

    @Override
    public void postProcessConnections(byte[] stateMap) {
        for (Direction side : DIR_AXIS_Y) {
            byte state = stateMap[side.ordinal()];
            if (ConnectionDirection.UP.isSet(state) || ConnectionDirection.DOWN.isSet(state)) {
                cleanConnections(stateMap, DIR_AXIS_Y, Direction.UP, Direction.DOWN, CON_MASK_AXIS_Y);
                return;
            }
        }
        for (Direction side : DIAG_DIR_AXIS_X) {
            if ((stateMap[side.ordinal()] & DIAG_CHECK_MASK) != 0) {
                cleanConnections(stateMap, DIR_AXIS_X, Direction.EAST, Direction.WEST, CON_MASK_DIAG_AXIS_X);
                return;
            }
        }
        for (Direction side : DIR_AXIS_X) {
            byte state = stateMap[side.ordinal()];
            if (ConnectionDirection.LEFT.isSet(state) || ConnectionDirection.RIGHT.isSet(state)) {
                cleanConnections(stateMap, DIR_AXIS_X, Direction.EAST, Direction.WEST, CON_MASK_AXIS_X);
                return;
            }
        }
        for (Direction side : DIAG_DIR_AXIS_Z) {
            if ((stateMap[side.ordinal()] & DIAG_CHECK_MASK) != 0) {
                cleanConnections(stateMap, DIR_AXIS_Z, Direction.NORTH, Direction.SOUTH, CON_MASK_DIAG_AXIS_Z);
                return;
            }
        }
        // If X and Y have no connections and Z is not blocked by adjacent X or Y pillars, it can only be Z or none, so no need to check or clean anything up
    }

    private static void cleanConnections(byte[] stateMap, Direction[] allowedDirs, Direction remOne, Direction remTwo, int conMask) {
        stateMap[remOne.ordinal()] = 0;
        stateMap[remTwo.ordinal()] = 0;

        for (int i = 0; i < 4; i++) {
            Direction side = allowedDirs[i];
            byte state = stateMap[side.ordinal()];
            stateMap[side.ordinal()] = (byte) (state & conMask);
        }
    }

    @Override
    public SpriteType getConnectedSprite(boolean xCon, boolean yCon, boolean diagCon, Direction side) {
        if (xCon) {
            return SpriteType.HORIZONTAL;
        }
        if (yCon) {
            return SpriteType.VERTICAL;
        }
        return SpriteType.NONE;
    }

    @Override
    public Set<SpriteType> getSpriteTypes() {
        return SPRITE_TYPES;
    }
}
