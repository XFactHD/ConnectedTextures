package io.github.xfacthd.contex.client.model;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.client.data.MetaEntry;
import io.github.xfacthd.contex.client.data.MetadataRegistry;
import io.github.xfacthd.contex.client.data.StatePredicate;
import io.github.xfacthd.contex.client.strategy.CompactTextureStrategy;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class ConTexBlockModelDefinition implements CustomBlockModelDefinition
{
    public static final MapCodec<ConTexBlockModelDefinition> CODEC = RecordCodecBuilder.<ConTexBlockModelDefinition>mapCodec(inst -> inst.group(
            BlockStateModelDispatcher.VANILLA_CODEC.forGetter(def -> def.baseDefinition),
            MetadataRegistry.STRATEGY_CODEC.optionalFieldOf("strategy", CompactTextureStrategy.INSTANCE).forGetter(def -> def.strategy),
            MetaEntry.CODEC.listOf().fieldOf("contex_meta").forGetter(def -> def.metadata)
    ).apply(inst, ConTexBlockModelDefinition::new)).validate(ConTexBlockModelDefinition::validate);

    private final BlockStateModelDispatcher baseDefinition;
    private final TextureStrategy strategy;
    private final List<MetaEntry> metadata;
    private final boolean metaNeedsFiltering;

    public ConTexBlockModelDefinition(BlockStateModelDispatcher baseDefinition, TextureStrategy strategy, List<MetaEntry> metadata)
    {
        this.baseDefinition = baseDefinition;
        this.strategy = strategy;
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
                    model -> new UnbakedConTexModel(entry.getKey(), model, strategy, getFilteredMetadata(entry.getKey()))
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

    private static DataResult<ConTexBlockModelDefinition> validate(ConTexBlockModelDefinition definition)
    {
        DataResult<List<MetaEntry>> result = MetaEntry.validate(definition.metadata, definition.strategy);
        return result.isError() ? result.map(_ -> definition) : DataResult.success(definition);
    }
}
