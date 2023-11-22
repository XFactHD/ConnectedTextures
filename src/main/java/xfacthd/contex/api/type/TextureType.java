package xfacthd.contex.api.type;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.state.ConnectionState;
import xfacthd.contex.api.utils.Constants;
import xfacthd.contex.api.utils.Utils;

import java.util.*;

public interface TextureType
{
    String ADDITIONAL_TEXTURE_KEY = "ct_texture";

    /**
     * {@return true if this type needs additional textures}
     */
    default boolean hasAdditionalTexture()
    {
        return true;
    }

    /**
     * {@return the {@link ResourceLocation} of an additional texture used for non-default connection states}
     */
    default ResourceLocation loadAdditionalTexture(JsonObject entry)
    {
        if (entry.has(ADDITIONAL_TEXTURE_KEY))
        {
            return Utils.getAsLocation(entry, ADDITIONAL_TEXTURE_KEY);
        }
        return null;
    }

    /**
     * {@return an {@link EnumSet} containing all directions this type operates on}
     */
    default EnumSet<Direction> getAffectedFaces()
    {
        return Constants.DIRECTIONS;
    }

    /**
     * Build a {@link ConnectionState} for the given {@linkplain Direction side} with the given {@link ConnectionPredicate}.<br>
     * Must only be called for sides contained in the set returned by {@link TextureType#getAffectedFaces()}
     * @param level The {@linkplain BlockAndTintGetter level} the block to be rendered is in
     * @param pos The {@link BlockPos} of the block to be rendered
     * @param state The {@link BlockState} of the block to be rendered
     * @param side The side of the block being asked for its quads
     * @param predicate The predicate used to check whether the block connects to its neighbors
     * @param texture The base texture from the CT entry this query originates from
     */
    ConnectionState getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            ResourceLocation texture
    );

    /**
     * Post-process
     */
    default void postProcessConnections(Map<Direction, ConnectionState> stateMap) { }

    /**
     * Create the {@link BakedQuad}s making up the given {@linkplain Direction side} of the block with the given {@link ConnectionState}.<br>
     * Must only be called for sides contained in the set returned by {@link TextureType#getAffectedFaces()}
     * @param srcQuad The original quad on the given side
     * @param side The side of the block
     * @param state The calculated connection state
     * @param ctTexture The additional texture to use for connections
     */
    List<BakedQuad> makeConnectionQuads(BakedQuad srcQuad, Direction side, ConnectionState state, ResourceLocation ctTexture);



    @ApiStatus.Internal
    static ResourceLocation loadAdditionalTexture(TextureType type, JsonObject entry, @Nullable ResourceLocation baseTexture)
    {
        if (!type.hasAdditionalTexture())
        {
            return null;
        }

        ResourceLocation ctTexture = type.loadAdditionalTexture(entry);
        if (ctTexture != null)
        {
            return ctTexture;
        }

        return baseTexture != null ? baseTexture.withSuffix("_ctm") : null;
    }
}
