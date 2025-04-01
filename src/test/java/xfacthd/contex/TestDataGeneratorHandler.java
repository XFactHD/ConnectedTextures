package xfacthd.contex;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xfacthd.contex.api.datagen.ConTexBlockModelDefinitionGenerator;
import xfacthd.contex.api.datagen.MetaEntryBuilder;
import xfacthd.contex.api.type.OcclusionMode;
import xfacthd.contex.api.utils.Constants;
import xfacthd.contex.client.predicate.SameBlockPredicate;
import xfacthd.contex.client.predicate.SameStatePredicate;
import xfacthd.contex.client.type.FullCarpetTextureType;
import xfacthd.contex.client.type.FullTextureType;
import xfacthd.contex.client.type.OmniPillarTextureType;
import xfacthd.contex.client.type.PillarTextureType;

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
        private final ResourceLocation TEX_OAK_LOG = mcLocation("block/oak_log");

        public TestBlockModelProvider(PackOutput output)
        {
            super(output, Constants.MOD_ID);
        }

        @Override
        protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
        {
            variant(blockModels, Blocks.CHISELED_DEEPSLATE,           builder -> builder.type(PillarTextureType.X).predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_DEEPSLATE));
            variant(blockModels, Blocks.CHISELED_POLISHED_BLACKSTONE, builder -> builder.type(PillarTextureType.Z).predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_BLACKSTONE));
            variant(blockModels, Blocks.CHISELED_STONE_BRICKS,        builder -> builder.type(PillarTextureType.Y).predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_STONEBRICKS));
            variant(blockModels, Blocks.GLASS,                        builder -> builder.type(FullTextureType.INSTANCE).predicate(SameBlockPredicate.INSTANCE).occlusionMode(OcclusionMode.SOLID_OR_SELF).addTexture(TEX_GLASS));
            variant(blockModels, Blocks.POLISHED_DIORITE,             builder -> builder.type(FullTextureType.INSTANCE).predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_DIORITE));
            variant(blockModels, Blocks.POLISHED_GRANITE,             builder -> builder.type(FullTextureType.INSTANCE).predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_GRANITE));
            variant(blockModels, Blocks.REDSTONE_BLOCK,               builder -> builder.type(OmniPillarTextureType.INSTANCE).predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_REDSTONE));

            TextureMapping mapping = new TextureMapping()
                    .put(SLOT_REDSTONE, TEX_REDSTONE)
                    .put(SLOT_GLASS, TEX_GLASS)
                    .put(TextureSlot.PARTICLE, TEX_REDSTONE);
            TEMPLATE_BLOCK.extend()
                    .element(elem -> elem.allFaces((dir, face) -> face.texture(SLOT_REDSTONE).cullface(dir).emissivity(15, 15)))
                    .element(elem -> elem.allFaces((dir, face) -> face.texture(SLOT_GLASS).cullface(dir)))
                    .renderType("cutout")
                    .build()
                    .create(Blocks.OAK_PLANKS, mapping, blockModels.modelOutput);
            variant(blockModels, Blocks.OAK_PLANKS, builder -> builder.type(FullTextureType.INSTANCE).predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_GLASS));

            ConTexBlockModelDefinitionGenerator slabGenerator = new ConTexBlockModelDefinitionGenerator(Blocks.OAK_SLAB);
            TextureMapping slabTextures = TextureMapping.column(TEX_DIORITE, TEX_DIORITE);
            ResourceLocation bottomSlab = ModelTemplates.SLAB_BOTTOM.create(Blocks.OAK_SLAB, slabTextures, blockModels.modelOutput);
            ResourceLocation topSlab = ModelTemplates.SLAB_TOP.create(Blocks.OAK_SLAB, slabTextures, blockModels.modelOutput);
            ResourceLocation doubleSlab = ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.OAK_SLAB, "_double", slabTextures, blockModels.modelOutput);
            slabGenerator.variant(MultiVariantGenerator.dispatch(Blocks.OAK_SLAB).with(
                    PropertyDispatch.initial(BlockStateProperties.SLAB_TYPE)
                            .select(SlabType.BOTTOM, BlockModelGenerators.plainVariant(bottomSlab))
                            .select(SlabType.TOP, BlockModelGenerators.plainVariant(topSlab))
                            .select(SlabType.DOUBLE, BlockModelGenerators.plainVariant(doubleSlab))
            ));
            slabGenerator.metadata(builder -> builder.type(FullTextureType.INSTANCE).predicate(SameStatePredicate.INSTANCE).addTexture(TEX_DIORITE));
            blockModels.blockStateOutput.accept(slabGenerator);

            TexturedModel.CARPET.get(Blocks.POLISHED_GRANITE).create(Blocks.RED_CARPET, blockModels.modelOutput);
            variant(blockModels, Blocks.RED_CARPET, builder -> builder.type(FullCarpetTextureType.Y).predicate(SameBlockPredicate.INSTANCE).occlusionMode(OcclusionMode.NONE).addTexture(TEX_GRANITE));

            ConTexBlockModelDefinitionGenerator logGenerator = new ConTexBlockModelDefinitionGenerator(Blocks.OAK_LOG);
            MultiVariant logVariant = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.OAK_LOG));
            MultiVariant logVariantHor = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.OAK_LOG, "_horizontal"));
            logGenerator.variant((MultiVariantGenerator) BlockModelGenerators.createRotatedPillarWithHorizontalVariant(Blocks.OAK_LOG, logVariant, logVariantHor));
            logGenerator.metadata(builder -> builder.type(PillarTextureType.X).predicate(SameStatePredicate.INSTANCE).addTexture(TEX_OAK_LOG).addStateFilter(BlockStateProperties.AXIS, Direction.Axis.X));
            logGenerator.metadata(builder -> builder.type(PillarTextureType.Y).predicate(SameStatePredicate.INSTANCE).addTexture(TEX_OAK_LOG).addStateFilter(BlockStateProperties.AXIS, Direction.Axis.Y));
            logGenerator.metadata(builder -> builder.type(PillarTextureType.Z).predicate(SameStatePredicate.INSTANCE).addTexture(TEX_OAK_LOG).addStateFilter(BlockStateProperties.AXIS, Direction.Axis.Z));
            blockModels.blockStateOutput.accept(logGenerator);
        }

        private static void variant(BlockModelGenerators blockModels, Block block, Consumer<MetaEntryBuilder> metaBuilder)
        {
            ConTexBlockModelDefinitionGenerator generator = new ConTexBlockModelDefinitionGenerator(block)
                    .variant(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block))))
                    .metadata(metaBuilder);
            blockModels.blockStateOutput.accept(generator);
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
