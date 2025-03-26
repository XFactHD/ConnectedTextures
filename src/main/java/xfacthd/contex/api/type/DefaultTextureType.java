package xfacthd.contex.api.type;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.model.*;
import xfacthd.contex.api.state.ConnectionDirection;
import xfacthd.contex.api.utils.Utils;

import java.util.*;

/**
 * Default texture type implementation which splits the faces into four quadrants and generates the appropriate quad
 * for a given quadrant. If a face has no connections, the incoming quad will be used and no modification is applied.
 * If a quadrant has no connections, the incoming quad's sprite is used, otherwise the given CT texture is used. The
 * relative connection UVs returned by {@link #getConnectionUVs(boolean, boolean, boolean, Direction)} are expected
 * to adhere to this convention.
 */
public abstract class DefaultTextureType extends TextureType
{
    @Override
    public final boolean hasAdditionalTexture()
    {
        return true;
    }

    @Override
    public List<BakedQuad> makeConnectionQuads(BakedQuad srcQuad, Direction side, byte state, ResourceLocation ctTexture)
    {
        List<BakedQuad> quads = new ArrayList<>(4);

        quads.add(makeConnectionQuad(srcQuad, side, state, ConnectionDirection.LEFT, ConnectionDirection.UP, ctTexture));
        quads.add(makeConnectionQuad(srcQuad, side, state, ConnectionDirection.RIGHT, ConnectionDirection.UP, ctTexture));
        quads.add(makeConnectionQuad(srcQuad, side, state, ConnectionDirection.LEFT, ConnectionDirection.DOWN, ctTexture));
        quads.add(makeConnectionQuad(srcQuad, side, state, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, ctTexture));

        quads.removeIf(Objects::isNull);
        return quads;
    }

    /**
     * {@return the relative UV coordinates (0-16) of the sprite section to use for the given connections on the given side}
     * @param xCon Whether the face has a connection on the X axis (top/bottom) or a horizontal connection (cardinal sides)
     * @param yCon Whether the face has a connection on the Z axis (top/bottom) or a vertical connection (cardinal sides)
     * @param diagCon Whether the face has a diagonal connection
     * @param side The side for which the UVs are requested
     */
    protected abstract UV getConnectionUVs(boolean xCon, boolean yCon, boolean diagCon, Direction side);

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
     * @return The resulting quad for the quadrant or null if the source quad being cut to the quadrant's size would
     *         result in an empty quad
     */
    @Nullable
    protected final BakedQuad makeConnectionQuad(
            BakedQuad srcQuad,
            Direction side,
            byte state,
            ConnectionDirection xDir,
            ConnectionDirection yDir,
            ResourceLocation ctTex
    )
    {
        boolean xCon = isSet(state, xDir);
        boolean yCon = isSet(state, yDir);
        boolean diagCon = isSet(state, ConnectionDirection.diagonal(xDir, yDir));

        boolean right = xDir == ConnectionDirection.RIGHT;
        boolean up = yDir == ConnectionDirection.UP;

        UV uvs = getConnectionUVs(xCon, yCon, diagCon, side);
        TextureAtlasSprite tex = (xCon || yCon) ? ModelUtils.getSprite(ctTex) : srcQuad.sprite();
        if (Utils.isY(side))
        {
            up ^= side == Direction.UP;
            return QuadModifier.of(srcQuad)
                    .apply(Modifiers.cutTopBottom(up ? Direction.SOUTH : Direction.NORTH, .5F))
                    .apply(Modifiers.cutTopBottom(right ? Direction.WEST : Direction.EAST, .5F))
                    .apply(Modifiers.remapTexture(tex, uvs.minU(), uvs.minV(), uvs.maxU(), uvs.maxV()))
                    .export();
        }
        else
        {
            return QuadModifier.of(srcQuad)
                    .apply(Modifiers.cutSideUpDown(up, .5F))
                    .apply(Modifiers.cutSideLeftRight(!right, .5F))
                    .apply(Modifiers.remapTexture(tex, uvs.minU(), uvs.minV(), uvs.maxU(), uvs.maxV()))
                    .export();
        }
    }
}
