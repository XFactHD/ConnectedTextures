package xfacthd.contex.client.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.state.ConnectionState;
import xfacthd.contex.api.type.TextureType;
import xfacthd.contex.client.data.*;
import xfacthd.contex.api.utils.Constants;

import java.time.Duration;
import java.util.*;

public sealed class ConTexModel extends BakedModelWrapper<BakedModel> permits SingleConTexModel
{
    private static final Duration DEFAULT_CACHE_DURATION = Duration.ofMinutes(10);

    private final Metadata metadata;
    private final Cache<QuadCacheKey, List<BakedQuad>> quadCache = Caffeine.newBuilder()
            .expireAfterAccess(DEFAULT_CACHE_DURATION)
            .executor(Util.backgroundExecutor())
            .build();

    public ConTexModel(BakedModel baseModel, Metadata metadata)
    {
        super(baseModel);
        this.metadata = metadata;
    }

    @Override
    public final List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            RandomSource rand,
            ModelData extraData,
            @Nullable RenderType renderType
    )
    {
        if (side == null || state == null || !extraData.has(Constants.CT_STATE_PROPERTY))
        {
            return super.getQuads(state, side, rand, extraData, renderType);
        }

        //noinspection ConstantConditions
        ConnectionStateContainer.StateMap ctStates = extraData.get(Constants.CT_STATE_PROPERTY).get(side);
        if (ctStates == null)
        {
            return super.getQuads(state, side, rand, extraData, renderType);
        }

        return quadCache.get(
                new QuadCacheKey(side, renderType, ctStates),
                key -> generateConnectionQuads(
                        key.ctStates, super.getQuads(state, key.side, rand, extraData, key.renderType), key.side
                )
        );
    }

    protected List<BakedQuad> generateConnectionQuads(
            ConnectionStateContainer.StateMap ctStates, List<BakedQuad> inputQuads, @Nullable Direction side
    )
    {
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad quad : inputQuads)
        {
            ResourceLocation tex = quad.getSprite().contents().name();
            collectQuads(
                    quads,
                    quad,
                    tex,
                    side,
                    metadata.get(tex),
                    ctStates.get(tex)
            );
        }
        return quads;
    }

    protected static void collectQuads(
            List<BakedQuad> quads,
            BakedQuad quad,
            ResourceLocation tex,
            Direction side,
            TextureEntry entry,
            ConnectionState conState
    )
    {
        if (entry == null || !entry.type().getAffectedFaces().contains(side) || !entry.baseTexture().equals(tex))
        {
            quads.add(quad);
            return;
        }
        quads.addAll(entry.type().makeConnectionQuads(quad, side, conState, entry.ctTexture()));
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
    {
        ConnectionStateContainer ctState = new ConnectionStateContainer();
        for (TextureEntry entry : metadata.getAll())
        {
            collectStateForEntry(level, pos, state, ctState, entry);
        }
        return modelData.derive().with(Constants.CT_STATE_PROPERTY, ctState).build();
    }

    protected static void collectStateForEntry(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            ConnectionStateContainer ctState,
            TextureEntry entry
    )
    {
        TextureType type = entry.type();
        Map<Direction, ConnectionState> stateMap = new EnumMap<>(Direction.class);
        for (Direction side : type.getAffectedFaces())
        {
            stateMap.put(side, type.getConnectionState(
                    level, pos, state, side, entry.predicate(), entry.baseTexture()
            ));
        }
        type.postProcessConnections(stateMap);
        for (Direction side : type.getAffectedFaces())
        {
            ConnectionState conState = stateMap.get(side);
            if (conState != null)
            {
                ctState.getOrCreate(side).put(entry.baseTexture(), conState);
            }
        }
    }

    private record QuadCacheKey(
            Direction side,
            RenderType renderType,
            ConnectionStateContainer.StateMap ctStates
    ) { }
}
