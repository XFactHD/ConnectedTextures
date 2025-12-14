package xfacthd.contex.api.datagen;

import com.google.common.base.Preconditions;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import xfacthd.contex.client.data.MetaEntry;
import xfacthd.contex.client.model.ConTexBlockModelDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ConTexBlockModelDefinitionGenerator implements BlockModelDefinitionGenerator
{
    private final Block block;
    private final List<MetaEntry> metadata = new ArrayList<>();
    @Nullable
    private MultiVariantGenerator variant = null;
    @Nullable
    private MultiPartGenerator multiPart = null;

    public ConTexBlockModelDefinitionGenerator(Block block)
    {
        this.block = block;
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

    public ConTexBlockModelDefinitionGenerator metadata(Consumer<MetaEntryBuilder> consumer)
    {
        MetaEntryBuilder builder = new MetaEntryBuilder();
        consumer.accept(builder);
        this.metadata.add(builder.build());
        return this;
    }

    @Override
    public Block block()
    {
        return block;
    }

    @Override
    public BlockModelDefinition create()
    {
        return new BlockModelDefinition(new ConTexBlockModelDefinition(
                new BlockModelDefinition(
                        Optional.ofNullable(variant).map(MultiVariantGenerator::create).flatMap(BlockModelDefinition::simpleModels),
                        Optional.ofNullable(multiPart).map(MultiPartGenerator::create).flatMap(BlockModelDefinition::multiPart)
                ),
                metadata
        ));
    }
}
