package xfacthd.contex.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;
import xfacthd.contex.client.data.MetaEntry;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ConTexBlockModelDefinition implements CustomBlockModelDefinition
{
    public static final MapCodec<ConTexBlockModelDefinition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockModelDefinition.VANILLA_CODEC.forGetter(def -> def.baseDefinition),
            MetaEntry.CODEC.listOf().fieldOf("contex_meta").validate(MetaEntry::validate).forGetter(def -> def.metadata)
    ).apply(inst, ConTexBlockModelDefinition::new));

    private final BlockModelDefinition baseDefinition;
    private final List<MetaEntry> metadata;

    public ConTexBlockModelDefinition(BlockModelDefinition baseDefinition, List<MetaEntry> metadata)
    {
        this.baseDefinition = baseDefinition;
        this.metadata = metadata;
    }

    @Override
    public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier)
    {
        Map<BlockState, BlockStateModel.UnbakedRoot> models = baseDefinition.instantiateVanilla(states, sourceSupplier);
        if (metadata.isEmpty()) return models;

        Map<BlockState, BlockStateModel.UnbakedRoot> newModels = new IdentityHashMap<>();
        Map<BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> wrappedModels = new IdentityHashMap<>();
        for (Map.Entry<BlockState, BlockStateModel.UnbakedRoot> entry : models.entrySet())
        {
            newModels.put(entry.getKey(), wrappedModels.computeIfAbsent(
                    entry.getValue(),
                    model -> new UnbakedConTexModel(model, metadata)
            ));
        }
        return newModels;
    }

    @Override
    public MapCodec<ConTexBlockModelDefinition> codec()
    {
        return CODEC;
    }
}
