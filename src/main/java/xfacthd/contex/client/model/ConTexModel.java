package xfacthd.contex.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.model.Modifiers;
import xfacthd.contex.api.model.QuadModifier;
import xfacthd.contex.api.type.TextureType;
import xfacthd.contex.api.utils.Utils;
import xfacthd.contex.client.data.*;
import xfacthd.contex.api.utils.Constants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ConTexModel extends BakedModelWrapper<BakedModel>
{
    private static final Direction[] DIRECTIONS = Direction.values();

    private final Map<QuadCacheKey, List<BakedQuad>> quadCache = new ConcurrentHashMap<>();
    private final MetaEntry[] metadata;
    private QuadTable srcQuads = null;

    public ConTexModel(BakedModel baseModel, List<MetaEntry> metadata)
    {
        super(baseModel);
        this.metadata = metadata.toArray(MetaEntry[]::new);
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            RandomSource rand,
            ModelData extraData,
            @Nullable RenderType renderType
    )
    {
        if (side == null || state == null || renderType == null || !extraData.has(Constants.CT_STATE_PROPERTY))
        {
            return super.getQuads(state, side, rand, extraData, renderType);
        }

        //noinspection ConstantConditions
        byte[] ctStates = extraData.get(Constants.CT_STATE_PROPERTY).get(side);
        if (ctStates == null)
        {
            return super.getQuads(state, side, rand, extraData, renderType);
        }

        QuadCacheKey key = new QuadCacheKey(side, renderType, ctStates);
        List<BakedQuad> quads = quadCache.get(key);
        if (quads == null)
        {
            quads = generateConnectionQuads(ctStates, state, side, renderType);
            quadCache.put(key, quads);
        }
        return quads;
    }

    private List<BakedQuad> generateConnectionQuads(byte[] ctStates, BlockState state, Direction side, RenderType renderType)
    {
        if (srcQuads == null)
        {
            srcQuads = decomposeBaseModel(state);
        }

        List<BakedQuad> quads = new ArrayList<>();
        for (QuadTable.Entry quadEntry : srcQuads.get(side, renderType))
        {
            int metaIdx = quadEntry.metaIdx();
            if (metaIdx == -1)
            {
                quads.add(quadEntry.quad());
                continue;
            }

            MetaEntry meta = metadata[metaIdx];
            quads.addAll(meta.type().makeConnectionQuads(
                    quadEntry.quad(), side, ctStates[metaIdx], meta.texture(quadEntry.texIdx()).ctTexture())
            );
        }
        return quads;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
    {
        ConnectionStateContainer ctState = new ConnectionStateContainer(metadata.length);
        for (int i = 0; i < metadata.length; i++)
        {
            MetaEntry entry = metadata[i];
            TextureType type = entry.type();
            byte[] stateMap = new byte[6];
            for (Direction side : type.getAffectedFaces())
            {
                stateMap[side.ordinal()] = type.getConnectionState(level, pos, state, side, entry.predicate(), entry.occlusionMode());
            }
            type.postProcessConnections(stateMap);
            for (Direction side : type.getAffectedFaces())
            {
                ctState.put(side, i, stateMap[side.ordinal()]);
            }
        }
        return modelData.derive().with(Constants.CT_STATE_PROPERTY, ctState).build();
    }

    private QuadTable decomposeBaseModel(BlockState state)
    {
        QuadTable srcQuads = new QuadTable();
        RandomSource random = RandomSource.create(42);
        for (RenderType renderType : originalModel.getRenderTypes(state, random, ModelData.EMPTY))
        {
            for (Direction side : DIRECTIONS)
            {
                random.setSeed(42);
                List<BakedQuad> quads = originalModel.getQuads(state, side, random, ModelData.EMPTY, renderType);
                ArrayList<QuadTable.Entry> decompQuads = new ArrayList<>(quads.size());
                for (BakedQuad quad : quads)
                {
                    QuadTable.Entry ctEntry = findCtEntry(quad);
                    if (ctEntry != null)
                    {
                        decompQuads.add(ctEntry);
                        continue;
                    }

                    //pre-emptively break apart non-CT quads to avoid z-fighting
                    makeNonCtQuads(decompQuads, quad, side);
                }
                srcQuads.put(side, renderType, decompQuads);
            }
        }
        return srcQuads;
    }

    @Nullable
    private QuadTable.Entry findCtEntry(BakedQuad quad)
    {
        for (int i = 0; i < metadata.length; i++)
        {
            int tex = metadata[i].findTexture(quad.getSprite().contents().name());
            if (tex != -1)
            {
                return new QuadTable.Entry(quad, i, tex);
            }
        }
        return null;
    }

    private static void makeNonCtQuads(ArrayList<QuadTable.Entry> decompQuads, BakedQuad quad, Direction side)
    {
        boolean y = Utils.isY(side);
        for (int i = 0; i < 4; i++)
        {
            boolean up = (i & 0b01) != 0;
            boolean right = (i & 0b10) != 0;
            BakedQuad quadOut = QuadModifier.of(quad)
                    .apply(y ? Modifiers.cutTopBottom(up ? Direction.SOUTH : Direction.NORTH, .5F) : Modifiers.cutSideUpDown(up, .5F))
                    .apply(y ? Modifiers.cutTopBottom(right ? Direction.WEST : Direction.EAST, .5F) : Modifiers.cutSideLeftRight(!right, .5F))
                    .apply(Modifiers.remapTexture(quad.getSprite(), 0F, 0F, 1F, 1F))
                    .export();
            decompQuads.add(new QuadTable.Entry(quadOut, -1, -1));
        }
    }
}
