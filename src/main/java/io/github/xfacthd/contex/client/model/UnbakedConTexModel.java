package io.github.xfacthd.contex.client.model;

import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.client.data.MetaEntry;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

final class UnbakedConTexModel implements BlockStateModel.UnbakedRoot {
    private final BlockState state;
    private final BlockStateModel.UnbakedRoot baseModel;
    private final TextureStrategy strategy;
    private final List<MetaEntry> metadata;
    private final Object bakingLock = new Object();
    @Nullable
    private volatile BlockStateModel cachedBakingResult = null;

    public UnbakedConTexModel(BlockState state, BlockStateModel.UnbakedRoot baseModel, TextureStrategy strategy, List<MetaEntry> metadata) {
        this.state = state;
        this.baseModel = baseModel;
        this.strategy = strategy;
        this.metadata = metadata;
    }

    @Override
    public BlockStateModel bake(BlockState ignoredState, ModelBaker baker) {
        // This cannot be converted to ModelBaker.SharedOperationKey due to the wrapped model potentially also using that
        if (cachedBakingResult == null) {
            synchronized (bakingLock) {
                if (cachedBakingResult == null) {
                    BlockStateModel bakedBase = baseModel.bake(state, baker);
                    if (metadata.isEmpty()) {
                        cachedBakingResult = bakedBase;
                    } else {
                        List<MetaEntry.Baked> bakedMetadata = metadata.stream()
                                .map(entry -> entry.bake(baker.materials(), strategy))
                                .toList();
                        cachedBakingResult = new ConTexModel(bakedBase, state, strategy, bakedMetadata);
                    }
                }
            }
        }
        return Objects.requireNonNull(cachedBakingResult);
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        baseModel.resolveDependencies(resolver);
    }

    @Override
    public Object visualEqualityGroup(BlockState state) {
        return this;
    }
}
