package xfacthd.contex;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.core.Holder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xfacthd.contex.api.model.builder.ConTexLoaderBuilder;
import xfacthd.contex.api.type.OcclusionMode;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Constants;

import java.util.function.Consumer;
import java.util.stream.Stream;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TestDataGeneratorHandler
{
    private TestDataGeneratorHandler() { }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent.Client event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(true, new TestBlockModelProvider(output));
    }



    private static final class TestBlockModelProvider extends ModelProvider
    {
        private static final TextureSlot SLOT_REDSTONE = TextureSlot.create("redstone");
        private static final TextureSlot SLOT_GLASS = TextureSlot.create("glass");
        private static final ModelTemplate TEMPLATE_BLOCK = ModelTemplates.create("block", SLOT_REDSTONE, SLOT_GLASS, TextureSlot.PARTICLE);

        private final ResourceLocation TEX_DEEPSLATE = mcLocation("block/chiseled_deepslate");
        private final ResourceLocation TEX_BLACKSTONE = mcLocation("block/chiseled_polished_blackstone");
        private final ResourceLocation TEX_STONEBRICKS = mcLocation("block/chiseled_stone_bricks");
        private final ResourceLocation TEX_GLASS = mcLocation("block/glass");
        private final ResourceLocation TEX_DIORITE = mcLocation("block/polished_diorite");
        private final ResourceLocation TEX_GRANITE = mcLocation("block/polished_granite");
        private final ResourceLocation TEX_REDSTONE = mcLocation("block/redstone_block");

        public TestBlockModelProvider(PackOutput output)
        {
            super(output, Constants.MOD_ID);
        }

        @Override
        protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
        {
            cubeAll(blockModels, Blocks.CHISELED_DEEPSLATE, TEX_DEEPSLATE, builder -> builder.addCtEntry(Builtin.Types.PILLAR_X, TEX_DEEPSLATE));

            cubeAll(blockModels, Blocks.CHISELED_POLISHED_BLACKSTONE, TEX_BLACKSTONE, builder -> builder.addCtEntry(Builtin.Types.PILLAR_Z, TEX_BLACKSTONE));

            cubeAll(blockModels, Blocks.CHISELED_STONE_BRICKS, TEX_STONEBRICKS, builder -> builder.addCtEntry(Builtin.Types.PILLAR_Y, TEX_STONEBRICKS));

            cubeAll(blockModels, Blocks.GLASS, TEX_GLASS, builder -> builder.addCtEntry(Builtin.Types.FULL, e -> e.addTexture(TEX_GLASS).occlusionMode(OcclusionMode.SOLID_OR_SELF)));

            cubeAll(blockModels, Blocks.POLISHED_DIORITE, TEX_DIORITE, builder -> builder.addCtEntry(Builtin.Types.FULL, TEX_DIORITE));

            cubeAll(blockModels, Blocks.POLISHED_GRANITE, TEX_GRANITE, builder -> builder.addCtEntry(Builtin.Types.FULL, TEX_GRANITE));

            cubeAll(blockModels, Blocks.REDSTONE_BLOCK, TEX_REDSTONE, builder -> builder.addCtEntry(Builtin.Types.PILLAR_OMNI, TEX_REDSTONE));

            TextureMapping mapping = new TextureMapping()
                    .put(SLOT_REDSTONE, TEX_REDSTONE)
                    .put(SLOT_GLASS, TEX_GLASS)
                    .put(TextureSlot.PARTICLE, TEX_REDSTONE);
            TEMPLATE_BLOCK.extend()
                    .customLoader(ConTexLoaderBuilder::new, builder ->
                            builder.addCtEntry(Builtin.Types.FULL, TEX_GLASS).optional()
                    )
                    .element(elem -> elem.allFaces((dir, face) -> face.texture(SLOT_REDSTONE).cullface(dir).emissivity(15, 15)))
                    .element(elem -> elem.allFaces((dir, face) -> face.texture(SLOT_GLASS).cullface(dir)))
                    .renderType("cutout")
                    .build()
                    .create(Blocks.OAK_PLANKS, mapping, blockModels.modelOutput);
        }

        private static void cubeAll(BlockModelGenerators blockModels, Block block, ResourceLocation texture, Consumer<ConTexLoaderBuilder> loaderBuilder)
        {
            ModelTemplates.CUBE_ALL.extend()
                    .customLoader(ConTexLoaderBuilder::new, builder ->
                    {
                        loaderBuilder.accept(builder);
                        builder.optional();
                    })
                    .build()
                    .create(block, TextureMapping.cube(texture), blockModels.modelOutput);
        }

        @Override
        protected Stream<? extends Holder<Block>> getKnownBlocks()
        {
            return Stream.empty();
        }

        @Override
        protected Stream<? extends Holder<Item>> getKnownItems()
        {
            return Stream.empty();
        }
    }
}
