package io.github.xfacthd.contex.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;
import io.github.xfacthd.contex.client.data.MetaEntry;
import io.github.xfacthd.contex.client.data.StatePredicate;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class ConTexBlockModelDefinition implements CustomBlockModelDefinition
{
    public static final MapCodec<ConTexBlockModelDefinition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockModelDefinition.VANILLA_CODEC.forGetter(def -> def.baseDefinition),
            MetaEntry.CODEC.listOf().fieldOf("contex_meta").validate(MetaEntry::validate).forGetter(def -> def.metadata)
    ).apply(inst, ConTexBlockModelDefinition::new));

    private final BlockModelDefinition baseDefinition;
    private final List<MetaEntry> metadata;
    private final boolean metaNeedsFiltering;

    public ConTexBlockModelDefinition(BlockModelDefinition baseDefinition, List<MetaEntry> metadata)
    {
        this.baseDefinition = baseDefinition;
        this.metadata = metadata;
        this.metaNeedsFiltering = metadata.stream().map(MetaEntry::statePredicate).anyMatch(Optional::isPresent);
    }

    @Override
    public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier)
    {
        Map<BlockState, BlockStateModel.UnbakedRoot> models = baseDefinition.instantiateVanilla(states, sourceSupplier);
        if (metadata.isEmpty()) return models;

        Map<BlockState, BlockStateModel.UnbakedRoot> newModels = new IdentityHashMap<>(models.size());
        Map<BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> wrappedModels = new IdentityHashMap<>(models.size());
        for (Map.Entry<BlockState, BlockStateModel.UnbakedRoot> entry : models.entrySet())
        {
            newModels.put(entry.getKey(), wrappedModels.computeIfAbsent(
                    entry.getValue(),
                    model -> new UnbakedConTexModel(entry.getKey(), model, getFilteredMetadata(entry.getKey()))
            ));
        }
        return newModels;
    }

    private List<MetaEntry> getFilteredMetadata(BlockState state)
    {
        if (metaNeedsFiltering)
        {
            List<MetaEntry> newMetadata = new ArrayList<>(metadata.size());
            for (MetaEntry entry : metadata)
            {
                Optional<StatePredicate> predicate = entry.statePredicate();
                if (predicate.isEmpty() || predicate.get().matches(state))
                {
                    newMetadata.add(entry);
                }
            }
            return newMetadata;
        }
        return metadata;
    }

    @Override
    public MapCodec<ConTexBlockModelDefinition> codec()
    {
        return CODEC;
    }
}
