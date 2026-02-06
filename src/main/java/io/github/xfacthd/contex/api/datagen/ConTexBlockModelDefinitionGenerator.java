package io.github.xfacthd.contex.api.datagen;

import com.google.common.base.Preconditions;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.client.data.MetaEntry;
import io.github.xfacthd.contex.client.model.ConTexBlockModelDefinition;
import io.github.xfacthd.contex.client.strategy.CompactTextureStrategy;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class ConTexBlockModelDefinitionGenerator implements BlockModelDefinitionGenerator
{
    private final Block block;
    private final TextureStrategy strategy;
    private final List<MetaEntry> metadata = new ArrayList<>();
    @Nullable
    private MultiVariantGenerator variant = null;
    @Nullable
    private MultiPartGenerator multiPart = null;

    public ConTexBlockModelDefinitionGenerator(Block block)
    {
        this(block, CompactTextureStrategy.INSTANCE);
    }

    public ConTexBlockModelDefinitionGenerator(Block block, TextureStrategy strategy)
    {
        this.block = block;
        this.strategy = strategy;
    }

    public ConTexBlockModelDefinitionGenerator variant(MultiVariantGenerator variant)
    {
        Preconditions.checkState(this.variant == null, "MultiVariantGenerator already set");
        this.variant = variant;
        return this;
    }

    public ConTexBlockModelDefinitionGenerator multiPart(MultiPartGenerator multiPart)
    {
        Preconditions.checkState(this.variant == null, "MultiPartGenerator already set");
        this.multiPart = multiPart;
        return this;
    }

    public ConTexBlockModelDefinitionGenerator metadata(TextureType type, UnaryOperator<MetaEntryBuilder> consumer)
    {
        this.metadata.add(consumer.apply(new MetaEntryBuilder(type, strategy)).build());
        return this;
    }

    @Override
    public Block block()
    {
        return block;
    }

    @Override
    public BlockStateModelDispatcher create()
    {
        return new BlockStateModelDispatcher(new ConTexBlockModelDefinition(
                new BlockStateModelDispatcher(
                        Optional.ofNullable(variant).map(MultiVariantGenerator::create).flatMap(BlockStateModelDispatcher::simpleModels),
                        Optional.ofNullable(multiPart).map(MultiPartGenerator::create).flatMap(BlockStateModelDispatcher::multiPart)
                ),
                strategy,
                metadata
        ));
    }
}
