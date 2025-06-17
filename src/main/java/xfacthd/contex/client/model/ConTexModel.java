package xfacthd.contex.client.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.model.Modifiers;
import xfacthd.contex.api.model.QuadModifier;
import xfacthd.contex.api.type.TextureType;
import xfacthd.contex.api.utils.Utils;
import xfacthd.contex.client.data.ConnectionStateContainer;
import xfacthd.contex.client.data.MetaEntry;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConTexModel extends DelegateBlockStateModel
{
    private static final Direction[] DIRECTIONS = Direction.values();

    private final Map<ConnectionStateContainer, List<BlockModelPart>> ctPartCache = new ConcurrentHashMap<>();
    private final BlockState state;
    private final MetaEntry[] metadata;
    private final Map<Object, List<ConnectedBlockModelPart>> decomposedPartsPerKey = new ConcurrentHashMap<>();

    ConTexModel(BlockStateModel baseModel, BlockState state, List<MetaEntry> metadata)
    {
        super(baseModel);
        this.state = state;
        this.metadata = metadata.toArray(MetaEntry[]::new);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts)
    {
        Object delegateGeometryKey = delegate.createGeometryKey(level, pos, state, random);
        ConnectionStateContainer ctStates = computeConnectionState(delegateGeometryKey, level, pos, state);
        List<BlockModelPart> ctParts = ctPartCache.get(ctStates);
        if (ctParts == null)
        {
            List<ConnectedBlockModelPart> decomposedParts = decomposedPartsPerKey.get(delegateGeometryKey);
            if (decomposedParts == null)
            {
                random.setSeed(state.getSeed(pos));
                decomposedParts = decomposeBaseModel(level, pos, random);
                decomposedPartsPerKey.put(delegateGeometryKey, decomposedParts);
            }

            ctParts = generateConnectionQuads(ctStates, decomposedParts);
            ctPartCache.put(ctStates, ctParts);
        }
        parts.addAll(ctParts);
    }

    private List<BlockModelPart> generateConnectionQuads(ConnectionStateContainer ctStates, List<ConnectedBlockModelPart> srcParts)
    {
        List<BlockModelPart> outParts = new ObjectArrayList<>();
        for (ConnectedBlockModelPart part : srcParts)
        {
            int metaIdx = part.metaIdx();
            int texIdx = part.texIdx();
            if (metaIdx == -1 || texIdx == -1)
            {
                outParts.add(part);
                continue;
            }

            MetaEntry meta = metadata[metaIdx];
            ResourceLocation ctTexture = meta.texture(texIdx).get(meta.type());

            QuadCollection.Builder quadsBuilder = new QuadCollection.Builder();
            for (Direction side : DIRECTIONS)
            {
                byte states = ctStates.get(side, metaIdx);
                for (BakedQuad quad : part.getQuads(side))
                {
                    List<BakedQuad> quads = meta.type().makeConnectionQuads(quad, side, states, ctTexture);
                    Utils.addQuads(quadsBuilder, side, quads);
                }
            }
            for (BakedQuad quad : part.getQuads(null))
            {
                Direction side = quad.direction();
                byte states = ctStates.get(side, metaIdx);
                List<BakedQuad> quads = meta.type().makeConnectionQuads(quad, side, states, ctTexture);
                Utils.addQuads(quadsBuilder, null, quads);
            }
            outParts.add(new SimpleModelWrapper(quadsBuilder.build(), part.useAmbientOcclusion(), part.particleIcon(), part.chunkLayer()));
        }
        return outParts;
    }

    @Override
    public Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random)
    {
        Object delegateGeometryKey = delegate.createGeometryKey(level, pos, state, random);
        return computeConnectionState(delegateGeometryKey, level, pos, state);
    }

    private ConnectionStateContainer computeConnectionState(
            @Nullable Object delegateGeometryKey,
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state
    )
    {
        ConnectionStateContainer ctState = new ConnectionStateContainer(this, metadata.length, delegateGeometryKey);
        byte[] stateMap = new byte[6];
        for (int i = 0; i < metadata.length; i++)
        {
            Arrays.fill(stateMap, (byte) 0);
            MetaEntry entry = metadata[i];
            TextureType type = entry.type();
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
        return ctState;
    }

    private List<ConnectedBlockModelPart> decomposeBaseModel(BlockAndTintGetter level, BlockPos pos, RandomSource random)
    {
        int affectedFaces = 0;
        for (MetaEntry meta : metadata)
        {
            for (Direction face : meta.type().getAffectedFaces())
            {
                affectedFaces |= 1 << face.ordinal();
            }
        }
        List<ConnectedBlockModelPart> outParts = new ObjectArrayList<>();
        for (BlockModelPart part : delegate.collectParts(level, pos, state, random))
        {
            QuadCollection.Builder preNonCtQuads = new QuadCollection.Builder();
            Map<MetaPair, QuadCollection.Builder> ctQuads = new Object2ObjectLinkedOpenHashMap<>();
            QuadCollection.Builder postNonCtQuads = new QuadCollection.Builder();
            int ctQuadsFound = 0;

            for (Direction side : DIRECTIONS)
            {
                int mask = 1 << side.ordinal();
                for (BakedQuad quad : part.getQuads(side))
                {
                    MetaPair meta = findCtEntry(quad);
                    if ((affectedFaces & mask) == 0)
                    {
                        preNonCtQuads.addCulledFace(side, quad);
                    }
                    else if (meta != null && meta.affectedFaces.contains(side))
                    {
                        ctQuadsFound |= mask;
                        ctQuads.computeIfAbsent(meta, $ -> new QuadCollection.Builder()).addCulledFace(side, quad);
                    }
                    else
                    {
                        boolean foundCt = (ctQuadsFound & mask) != 0;
                        makeNonCtQuads(foundCt ? postNonCtQuads : preNonCtQuads, quad, side);
                    }
                }
            }
            for (BakedQuad quad : part.getQuads(null))
            {
                MetaPair meta = findCtEntry(quad);
                if ((affectedFaces & (1 << quad.direction().ordinal())) == 0)
                {
                    preNonCtQuads.addUnculledFace(quad);
                }
                else if (meta != null && meta.affectedFaces.contains(quad.direction()))
                {
                    ctQuadsFound |= 0b01000000;
                    ctQuads.computeIfAbsent(meta, $ -> new QuadCollection.Builder()).addUnculledFace(quad);
                }
                else
                {
                    boolean foundCt = (ctQuadsFound & 0b01000000) != 0;
                    makeNonCtQuads(foundCt ? postNonCtQuads : preNonCtQuads, quad, null);
                }
            }

            QuadCollection preQuads = preNonCtQuads.build();
            if (!preQuads.getAll().isEmpty())
            {
                outParts.add(ConnectedBlockModelPart.of(part, state, preQuads, -1, -1));
            }
            for (Map.Entry<MetaPair, QuadCollection.Builder> entry : ctQuads.entrySet())
            {
                QuadCollection quads = entry.getValue().build();
                if (quads.getAll().isEmpty()) continue;

                MetaPair meta = entry.getKey();
                outParts.add(ConnectedBlockModelPart.of(part, state, quads, meta.metaIdx, meta.texIdx));
            }
            QuadCollection postQuads = postNonCtQuads.build();
            if (!postQuads.getAll().isEmpty())
            {
                outParts.add(ConnectedBlockModelPart.of(part, state, postQuads, -1, -1));
            }
        }
        return outParts;
    }

    @Nullable
    private MetaPair findCtEntry(BakedQuad quad)
    {
        for (int i = 0; i < metadata.length; i++)
        {
            int tex = metadata[i].findTexture(quad.sprite().contents().name());
            if (tex != -1)
            {
                return new MetaPair(i, tex, metadata[i].type().getAffectedFaces());
            }
        }
        return null;
    }

    private static void makeNonCtQuads(QuadCollection.Builder decompQuads, BakedQuad quad, @Nullable Direction side)
    {
        boolean y = Utils.isY(quad.direction());
        for (int i = 0; i < 4; i++)
        {
            boolean up = (i & 0b01) != 0;
            boolean right = (i & 0b10) != 0;
            BakedQuad quadOut = QuadModifier.of(quad)
                    .apply(y ? Modifiers.cutTopBottom(up ? Direction.SOUTH : Direction.NORTH, .5F) : Modifiers.cutSideUpDown(up, .5F))
                    .apply(y ? Modifiers.cutTopBottom(right ? Direction.WEST : Direction.EAST, .5F) : Modifiers.cutSideLeftRight(!right, .5F))
                    .export();
            if (quadOut != null)
            {
                if (side != null)
                {
                    decompQuads.addCulledFace(side, quadOut);
                }
                else
                {
                    decompQuads.addUnculledFace(quadOut);
                }
            }
        }
    }

    private record MetaPair(int metaIdx, int texIdx, EnumSet<Direction> affectedFaces) { }
}
