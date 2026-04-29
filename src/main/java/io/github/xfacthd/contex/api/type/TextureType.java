package io.github.xfacthd.contex.api.type;

import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.utils.Constants;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.Set;

public abstract class TextureType {
    /// Returns a [Set] containing all [SpriteType]s this texture type uses to compose connected faces.
    /// All types returned by this method must be [SpriteType#BASE_TYPES].
    ///
    /// @return the sprite types used by this texture type
    public abstract Set<SpriteType> getSpriteTypes();

    /**
     * {@return an {@link EnumSet} containing all directions this type operates on}
     */
    public EnumSet<Direction> getAffectedFaces() {
        return Constants.DIRECTIONS;
    }

    /**
     * Build a connection state for the given {@linkplain Direction side} with the given {@link ConnectionPredicate}.<br>
     * Must only be called for sides contained in the set returned by {@link TextureType#getAffectedFaces()}
     *
     * @param level         The {@linkplain BlockAndTintGetter level} the block to be rendered is in
     * @param pos           The {@link BlockPos} of the block to be rendered
     * @param state         The {@link BlockState} of the block to be rendered
     * @param side          The side of the block being asked for its quads
     * @param predicate     The predicate used to check whether the block connects to its neighbors
     * @param occlusionMode The occlusion mode to use for connection occlusion
     */
    public abstract byte getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    );

    /**
     * Post-process
     */
    public void postProcessConnections(byte[] stateMap) { }

    /**
     * {@return the {@link SpriteType} of the connected sprite or null for the unconnected texture}
     *
     * @param xCon    Whether the face has a connection on the X axis (top/bottom) or a horizontal connection (cardinal sides)
     * @param yCon    Whether the face has a connection on the Z axis (top/bottom) or a vertical connection (cardinal sides)
     * @param diagCon Whether the face has a diagonal connection
     * @param side    The side for which the UVs are requested
     */
    public abstract SpriteType getConnectedSprite(boolean xCon, boolean yCon, boolean diagCon, Direction side);

    protected static byte testDirection(
            ConnectionDirection dir,
            byte connections,
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    ) {
        BlockPos otherPos = pos.offset(dir.getOffset(side));
        if (!predicate.test(level, pos, otherPos, state, side, side)) {
            return connections;
        }

        if (isConnectionVisible(level, otherPos, side, predicate, occlusionMode)) {
            return dir.set(connections);
        }
        return connections;
    }

    /**
     * Check whether the connection on the given side of the block being connected to is visible
     *
     * @param level         The level the block is in
     * @param conPos        The position of the block being connected to
     * @param side          The side of the block being connected to
     * @param predicate     The connection predicate being used to test for the occluding state matching the potentially occluded state
     * @param occlusionMode The occlusion mode to use for checking occlusion
     */
    protected static boolean isConnectionVisible(
            BlockAndTintGetter level,
            BlockPos conPos,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    ) {
        if (occlusionMode != OcclusionMode.NONE) {
            BlockPos occludePos = conPos.relative(side);
            BlockState state = level.getBlockState(conPos);
            if (occlusionMode.isOccludedBySelf() && predicate.test(level, conPos, occludePos, state, side, side.getOpposite())) {
                return false;
            } else if (occlusionMode.isOccludedBySolid()) {
                return Block.shouldRenderFace(level, conPos, state, level.getBlockState(occludePos), side);
            }
        }
        return true;
    }
}
