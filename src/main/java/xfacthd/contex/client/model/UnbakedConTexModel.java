package xfacthd.contex.client.model;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.client.data.MetaEntry;

import java.util.List;

final class UnbakedConTexModel implements BlockStateModel.UnbakedRoot
{
    private final BlockStateModel.UnbakedRoot baseModel;
    private final List<MetaEntry> metadata;

    public UnbakedConTexModel(BlockStateModel.UnbakedRoot baseModel, List<MetaEntry> metadata)
    {
        this.baseModel = baseModel;
        this.metadata = metadata;
    }

    @Override
    public BlockStateModel bake(BlockState state, ModelBaker baker)
    {
        BlockStateModel bakedBase = baseModel.bake(state, baker);
        return metadata.isEmpty() ? bakedBase : new ConTexModel(bakedBase, state, metadata);
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
