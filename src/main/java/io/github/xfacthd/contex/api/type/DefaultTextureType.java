package io.github.xfacthd.contex.api.type;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.model.ModelUtils;
import io.github.xfacthd.contex.api.model.Modifiers;
import io.github.xfacthd.contex.api.model.QuadModifier;
import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.utils.Utils;

import java.util.function.Consumer;

/**
 * Default texture type implementation which splits the faces into four quadrants and generates the appropriate quad
 * for a given quadrant. If a face has no connections, the incoming quad will be used and no modification is applied.
 * If a quadrant has no connections, the incoming quad's sprite is used, otherwise the given CT texture is used. The
 * relative connection UVs returned by {@link #getConnectionUVs(boolean, boolean, boolean, Direction)} are expected
 * to adhere to this convention.
 * FIXME: removal of UV shrinking makes "texture map"-style compact CT no longer viable (shows seams), replace with separate sprite per variant
 */
public abstract class DefaultTextureType extends TextureType
{
    @Override
    public final boolean hasAdditionalTexture()
    {
        return true;
    }

    @Override
    public void makeConnectionQuads(BakedQuad srcQuad, Direction side, byte state, Identifier ctTexture, Consumer<BakedQuad> output)
    {
        makeConnectionQuad(output, srcQuad, side, state, ConnectionDirection.LEFT, ConnectionDirection.UP, ctTexture);
        makeConnectionQuad(output, srcQuad, side, state, ConnectionDirection.RIGHT, ConnectionDirection.UP, ctTexture);
        makeConnectionQuad(output, srcQuad, side, state, ConnectionDirection.LEFT, ConnectionDirection.DOWN, ctTexture);
        makeConnectionQuad(output, srcQuad, side, state, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, ctTexture);
    }

    /**
     * {@return the relative UV coordinates (0-16) of the sprite section to use for the given connections on the given side}
     * @param xCon Whether the face has a connection on the X axis (top/bottom) or a horizontal connection (cardinal sides)
     * @param yCon Whether the face has a connection on the Z axis (top/bottom) or a vertical connection (cardinal sides)
     * @param diagCon Whether the face has a diagonal connection
     * @param side The side for which the UVs are requested
     */
    protected abstract BlockElementFace.UVs getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side);

    /**
     * Create a {@link BakedQuad} facing in the given direction for the corner represented by the two given
     * {@link ConnectionDirection}s based on the given source quad of the given side. If the quadrant has at least an
     * X and/or Y connection, the given CT texture is used, otherwise the texture of the incoming quad is used.
     * Diagonal connections are ignored or neither of the axis-aligned connections are present.
     * @param srcQuad The source quad
     * @param side The side the quad is on
     * @param state The connection state of the given side
     * @param xDir The horizontal direction of the corner the resulting quad will cover
     * @param yDir The vertical direction of the corner the resulting quad will cover
     * @param ctTex The texture location to use if the quadrant has at least an X and/or Y connection
     */
    protected final void makeConnectionQuad(
            Consumer<BakedQuad> output,
            BakedQuad srcQuad,
            Direction side,
            byte state,
            ConnectionDirection xDir,
            ConnectionDirection yDir,
            Identifier ctTex
    )
    {
        boolean xCon = xDir.isSet(state);
        boolean yCon = yDir.isSet(state);
        boolean diagCon = ConnectionDirection.diagonal(xDir, yDir).isSet(state);

        boolean right = xDir == ConnectionDirection.RIGHT;
        boolean up = yDir == ConnectionDirection.UP;

        BlockElementFace.UVs uvs = getConnectionUVs(xCon, yCon, diagCon, side);
        TextureAtlasSprite tex = (xCon || yCon) ? ModelUtils.getSprite(ctTex) : srcQuad.sprite();
        BakedQuad result;
        if (Utils.isY(side))
        {
            up ^= side == Direction.UP;
            result = QuadModifier.of(srcQuad)
                    .apply(Modifiers.cutTopBottom(up ? Direction.SOUTH : Direction.NORTH, .5F))
                    .apply(Modifiers.cutTopBottom(right ? Direction.WEST : Direction.EAST, .5F))
                    .apply(Modifiers.remapTexture(tex, uvs))
                    .export();
        }
        else
        {
            result = QuadModifier.of(srcQuad)
                    .apply(Modifiers.cutSideUpDown(up, .5F))
                    .apply(Modifiers.cutSideLeftRight(!right, .5F))
                    .apply(Modifiers.remapTexture(tex, uvs))
                    .export();
        }
        if (result != null)
        {
            output.accept(result);
        }
    }

    protected static byte testDirection(
            ConnectionDirection dir,
            byte connections,
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    )
    {
        BlockPos otherPos = pos.offset(dir.getOffset(side));
        if (!predicate.test(level, pos, otherPos, state, side, side))
        {
            return connections;
        }

        if (isConnectionVisible(level, otherPos, side, predicate, occlusionMode))
        {
            return dir.set(connections);
        }
        return connections;
    }
}
