package xfacthd.contex.client.model;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.client.data.MetaEntry;

import java.util.List;
import java.util.Objects;

final class UnbakedConTexModel implements BlockStateModel.UnbakedRoot
{
    private final BlockState state;
    private final BlockStateModel.UnbakedRoot baseModel;
    private final List<MetaEntry> metadata;
    private final Object bakingLock = new Object();
    @Nullable
    private volatile BlockStateModel cachedBakingResult = null;

    public UnbakedConTexModel(BlockState state, BlockStateModel.UnbakedRoot baseModel, List<MetaEntry> metadata)
    {
        this.state = state;
        this.baseModel = baseModel;
        this.metadata = metadata;
    }

    @Override
    public BlockStateModel bake(BlockState ignoredState, ModelBaker baker)
    {
        // This cannot be converted to ModelBaker.SharedOperationKey due to the wrapped model potentially also using that
        if (cachedBakingResult == null)
        {
            synchronized (bakingLock)
            {
                if (cachedBakingResult == null)
                {
                    BlockStateModel bakedBase = baseModel.bake(state, baker);
                    cachedBakingResult = metadata.isEmpty() ? bakedBase : new ConTexModel(bakedBase, state, metadata);
                }
            }
        }
        return Objects.requireNonNull(cachedBakingResult);
    }

    @Override
    public void resolveDependencies(Resolver resolver)
    {
        baseModel.resolveDependencies(resolver);
    }

    @Override
    public Object visualEqualityGroup(BlockState state)
    {
        return this;
    }
}
