package xfacthd.contex.api.type;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.utils.Constants;

import java.util.*;

public abstract class TextureType
{
    public static final String BASE_TEXTURE_KEY = "main_texture";
    public static final String ADDITIONAL_TEXTURE_KEY = "ct_texture";

    /**
     * {@return true if this type needs additional textures}
     */
    public boolean hasAdditionalTexture()
    {
        return true;
    }

    /**
     * {@return an {@link EnumSet} containing all directions this type operates on}
     */
    public EnumSet<Direction> getAffectedFaces()
    {
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
     * Create the {@link BakedQuad}s making up the given {@linkplain Direction side} of the block with the given connection state.<br>
     * Must only be called for sides contained in the set returned by {@link TextureType#getAffectedFaces()}
     *
     * @param srcQuad   The original quad on the given side
     * @param side      The side of the block
     * @param state     The calculated connection state
     * @param ctTexture The additional texture to use for connections
     */
    public abstract List<BakedQuad> makeConnectionQuads(BakedQuad srcQuad, Direction side, byte state, ResourceLocation ctTexture);

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
    )
    {
        if (occlusionMode != OcclusionMode.NONE)
        {
            BlockPos occludePos = conPos.relative(side);
            BlockState state = level.getBlockState(conPos);
            if (occlusionMode.isOccludedBySelf() && predicate.test(level, conPos, occludePos, state, side, side.getOpposite()))
            {
                return false;
            }
            else if (occlusionMode.isOccludedBySolid())
            {
                return Block.shouldRenderFace(state, level, conPos, side, occludePos);
            }
        }
        return true;
    }

    /**
     * Check whether the given {@link ConnectionDirection} is set on the given connection state
     */
    protected static boolean isSet(byte connections, ConnectionDirection dir)
    {
        return (connections & (1 << dir.ordinal())) != 0;
    }

    /**
     * Set the given {@link ConnectionDirection} on the given connection state
     */
    protected static byte set(byte connections, ConnectionDirection dir)
    {
        return (byte) (connections | (byte) (1 << dir.ordinal()));
    }
}
