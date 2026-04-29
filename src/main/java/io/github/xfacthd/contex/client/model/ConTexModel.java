package io.github.xfacthd.contex.client.model;

import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.client.data.ConnectionStateContainer;
import io.github.xfacthd.contex.client.data.MetaEntry;
import io.github.xfacthd.contex.client.data.TextureEntry;
import io.github.xfacthd.contex.client.util.ExtendedQuadCollectionBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ConTexModel extends DelegateBlockStateModel {
    private static final Direction[] DIRECTIONS = Direction.values();
    /// Placeholder for `null` return values from [BlockStateModel#createGeometryKey(BlockAndTintGetter, BlockPos, BlockState, RandomSource)]
    /// due to [ConcurrentHashMap] not supporting `null` keys
    private static final Object NULL_KEY_DUMMY = new Object();

    private final Map<ConnectionStateContainer, List<BlockStateModelPart>> ctPartCache = new ConcurrentHashMap<>();
    private final BlockState state;
    private final TextureStrategy strategy;
    private final MetaEntry.Baked[] metadata;
    private final Map<Object, List<ConnectedBlockStateModelPart>> decomposedPartsPerKey = new ConcurrentHashMap<>();

    ConTexModel(BlockStateModel baseModel, BlockState state, TextureStrategy strategy, List<MetaEntry.Baked> metadata) {
        super(baseModel);
        this.state = state;
        this.strategy = strategy;
        this.metadata = metadata.toArray(MetaEntry.Baked[]::new);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        Object delegateGeometryKey = Objects.requireNonNullElse(delegate.createGeometryKey(level, pos, state, random), NULL_KEY_DUMMY);
        ConnectionStateContainer ctStates = computeConnectionState(delegateGeometryKey, level, pos, state);
        List<BlockStateModelPart> ctParts = ctPartCache.get(ctStates);
        if (ctParts == null) {
            List<ConnectedBlockStateModelPart> decomposedParts = decomposedPartsPerKey.get(delegateGeometryKey);
            if (decomposedParts == null) {
                random.setSeed(state.getSeed(pos));
                decomposedParts = decomposeBaseModel(level, pos, random);
                decomposedPartsPerKey.put(delegateGeometryKey, decomposedParts);
            }

            ctParts = generateConnectionQuads(ctStates, decomposedParts);
            ctPartCache.put(ctStates, ctParts);
        }
        parts.addAll(ctParts);
    }

    private List<BlockStateModelPart> generateConnectionQuads(ConnectionStateContainer ctStates, List<ConnectedBlockStateModelPart> srcParts) {
        List<BlockStateModelPart> outParts = new ObjectArrayList<>(srcParts.size());
        for (ConnectedBlockStateModelPart part : srcParts) {
            int metaIdx = part.metaIdx();
            int texIdx = part.texIdx();
            if (metaIdx == -1 || texIdx == -1) {
                outParts.add(part);
                continue;
            }

            MetaEntry.Baked meta = metadata[metaIdx];
            TextureType texType = meta.type();
            TextureEntry.Baked ctTextures = meta.texture(texIdx);

            ExtendedQuadCollectionBuilder quadsBuilder = new ExtendedQuadCollectionBuilder();
            for (Direction side : DIRECTIONS) {
                quadsBuilder.setCullFace(side);
                byte states = ctStates.get(side, metaIdx);
                for (BakedQuad quad : part.getQuads(side)) {
                    strategy.makeConnectionQuads(texType, quad, side, states, ctTextures, quadsBuilder);
                }
            }
            quadsBuilder.setCullFace(null);
            for (BakedQuad quad : part.getQuads(null)) {
                Direction side = quad.direction();
                byte states = ctStates.get(side, metaIdx);
                strategy.makeConnectionQuads(texType, quad, side, states, ctTextures, quadsBuilder);
            }
            outParts.add(new SimpleModelWrapper(quadsBuilder.build(), part.useAmbientOcclusion(), part.particleMaterial()));
        }
        return outParts;
    }

    @Override
    public Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        Object delegateGeometryKey = delegate.createGeometryKey(level, pos, state, random);
        return computeConnectionState(delegateGeometryKey, level, pos, state);
    }

    private ConnectionStateContainer computeConnectionState(
            @Nullable Object delegateGeometryKey,
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state
    ) {
        ConnectionStateContainer ctState = new ConnectionStateContainer(this, metadata.length, delegateGeometryKey);
        byte[] stateMap = new byte[6];
        for (int i = 0; i < metadata.length; i++) {
            Arrays.fill(stateMap, (byte) 0);
            MetaEntry.Baked entry = metadata[i];
            TextureType type = entry.type();
            for (Direction side : type.getAffectedFaces()) {
                stateMap[side.ordinal()] = type.getConnectionState(level, pos, state, side, entry.predicate(), entry.occlusionMode());
            }
            type.postProcessConnections(stateMap);
            for (Direction side : type.getAffectedFaces()) {
                ctState.put(side, i, stateMap[side.ordinal()]);
            }
        }
        return ctState;
    }

    private List<ConnectedBlockStateModelPart> decomposeBaseModel(BlockAndTintGetter level, BlockPos pos, RandomSource random) {
        int affectedFaces = 0;
        for (MetaEntry.Baked meta : metadata) {
            for (Direction face : meta.type().getAffectedFaces()) {
                affectedFaces |= 1 << face.ordinal();
            }
        }
        List<ConnectedBlockStateModelPart> outParts = new ObjectArrayList<>();
        List<BlockStateModelPart> srcParts = new ObjectArrayList<>();
        delegate.collectParts(level, pos, state, random, srcParts);
        for (BlockStateModelPart part : srcParts) {
            ExtendedQuadCollectionBuilder preNonCtQuads = new ExtendedQuadCollectionBuilder();
            Map<MetaPair, QuadCollection.Builder> ctQuads = new Object2ObjectLinkedOpenHashMap<>();
            ExtendedQuadCollectionBuilder postNonCtQuads = new ExtendedQuadCollectionBuilder();
            int ctQuadsFound = 0;

            for (Direction side : DIRECTIONS) {
                preNonCtQuads.setCullFace(side);
                postNonCtQuads.setCullFace(side);
                int mask = 1 << side.ordinal();
                for (BakedQuad quad : part.getQuads(side)) {
                    MetaPair meta = findCtEntry(quad);
                    if ((affectedFaces & mask) == 0) {
                        preNonCtQuads.addCulledFace(side, quad);
                    } else if (meta != null && meta.affectedFaces.contains(side)) {
                        ctQuadsFound |= mask;
                        ctQuads.computeIfAbsent(meta, _ -> new QuadCollection.Builder()).addCulledFace(side, quad);
                    } else {
                        boolean foundCt = (ctQuadsFound & mask) != 0;
                        strategy.makeNonCtQuads(quad, foundCt ? postNonCtQuads : preNonCtQuads);
                    }
                }
            }
            preNonCtQuads.setCullFace(null);
            postNonCtQuads.setCullFace(null);
            for (BakedQuad quad : part.getQuads(null)) {
                MetaPair meta = findCtEntry(quad);
                if ((affectedFaces & (1 << quad.direction().ordinal())) == 0) {
                    preNonCtQuads.addUnculledFace(quad);
                } else if (meta != null && meta.affectedFaces.contains(quad.direction())) {
                    ctQuadsFound |= 0b01000000;
                    ctQuads.computeIfAbsent(meta, _ -> new QuadCollection.Builder()).addUnculledFace(quad);
                } else {
                    boolean foundCt = (ctQuadsFound & 0b01000000) != 0;
                    strategy.makeNonCtQuads(quad, foundCt ? postNonCtQuads : preNonCtQuads);
                }
            }

            QuadCollection preQuads = preNonCtQuads.build();
            if (!preQuads.getAll().isEmpty()) {
                outParts.add(ConnectedBlockStateModelPart.of(part, preQuads, -1, -1));
            }
            for (Map.Entry<MetaPair, QuadCollection.Builder> entry : ctQuads.entrySet()) {
                QuadCollection quads = entry.getValue().build();
                if (quads.getAll().isEmpty()) {
                    continue;
                }

                MetaPair meta = entry.getKey();
                outParts.add(ConnectedBlockStateModelPart.of(part, quads, meta.metaIdx, meta.texIdx));
            }
            QuadCollection postQuads = postNonCtQuads.build();
            if (!postQuads.getAll().isEmpty()) {
                outParts.add(ConnectedBlockStateModelPart.of(part, postQuads, -1, -1));
            }
        }
        return outParts;
    }

    @Nullable
    private MetaPair findCtEntry(BakedQuad quad) {
        for (int i = 0; i < metadata.length; i++) {
            int tex = metadata[i].findTexture(quad.materialInfo().sprite());
            if (tex != -1) {
                return new MetaPair(i, tex, metadata[i].type().getAffectedFaces());
            }
        }
        return null;
    }

    private record MetaPair(int metaIdx, int texIdx, EnumSet<Direction> affectedFaces) { }
}
