package io.github.xfacthd.contex;

import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.api.utils.Utils;
import net.minecraft.SharedConstants;
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
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import io.github.xfacthd.contex.api.datagen.ConTexBlockModelDefinitionGenerator;
import io.github.xfacthd.contex.api.datagen.MetaEntryBuilder;
import io.github.xfacthd.contex.api.utils.Constants;
import io.github.xfacthd.contex.client.predicate.SameBlockPredicate;
import io.github.xfacthd.contex.client.predicate.SameStatePredicate;
import io.github.xfacthd.contex.api.texture.Border;
import io.github.xfacthd.contex.api.texture.ConTexSpriteSource;
import io.github.xfacthd.contex.client.type.FullCarpetTextureType;
import io.github.xfacthd.contex.client.type.FullTextureType;
import io.github.xfacthd.contex.client.type.OmniPillarTextureType;
import io.github.xfacthd.contex.client.type.PillarTextureType;
import io.github.xfacthd.contex.client.type.RotatingPillarTextureType;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public final class TestDataGeneratorHandler
{
    private static final String TEST_PACK_ID = "test_ct";

    public TestDataGeneratorHandler(IEventBus modBus)
    {
        modBus.addListener(TestDataGeneratorHandler::onGatherData);
        modBus.addListener(TestDataGeneratorHandler::onAddPackFinders);
    }

    private static void onAddPackFinders(AddPackFindersEvent event)
    {
        event.addPackFinders(
                Utils.rl(TEST_PACK_ID),
                PackType.CLIENT_RESOURCES,
                Component.literal("ConTex Test Pack"),
                PackSource.DEFAULT,
                true,
                Pack.Position.TOP
        );
    }

    private static void onGatherData(final GatherDataEvent.Client event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput(TEST_PACK_ID);
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new PackMetadataGenerator(output).add(
                PackMetadataSection.CLIENT_TYPE,
                new PackMetadataSection(
                        ConnectedTextures.BUILTIN_RP_DESC,
                        SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange()
                )
        ));
        generator.addProvider(true, new TestBlockModelProvider(output));
        generator.addProvider(true, new TestSpriteSourceProvider(output, lookupProvider));
    }

    private static final class TestBlockModelProvider extends ModelProvider
    {
        private static final TextureSlot SLOT_REDSTONE = TextureSlot.create("redstone");
        private static final TextureSlot SLOT_GLASS = TextureSlot.create("glass");
        private static final ModelTemplate TEMPLATE_BLOCK = ModelTemplates.create("block", SLOT_REDSTONE, SLOT_GLASS, TextureSlot.PARTICLE);

        private final Identifier TEX_DEEPSLATE = mcLocation("block/chiseled_deepslate");
        private final Identifier TEX_BLACKSTONE = mcLocation("block/chiseled_polished_blackstone");
        private final Identifier TEX_STONEBRICKS = mcLocation("block/chiseled_stone_bricks");
        private final Identifier TEX_GLASS = mcLocation("block/glass");
        private final Identifier TEX_DIORITE = mcLocation("block/polished_diorite");
        private final Identifier TEX_GRANITE = mcLocation("block/polished_granite");
        private final Identifier TEX_REDSTONE = mcLocation("block/redstone_block");
        private final Identifier TEX_SEA_LANTERN = mcLocation("block/sea_lantern");
        private final Identifier TEX_RED_WOOL = mcLocation("block/red_wool");
        private final Identifier TEX_OAK_LOG = mcLocation("block/oak_log");
        private final Identifier TEX_STONE = mcLocation("block/stone");

        public TestBlockModelProvider(PackOutput output)
        {
            super(output, Constants.MOD_ID);
        }

        @Override
        protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
        {
            variant(blockModels, Blocks.CHISELED_DEEPSLATE, RotatingPillarTextureType.X,           builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_DEEPSLATE));
            variant(blockModels, Blocks.CHISELED_POLISHED_BLACKSTONE, RotatingPillarTextureType.Z, builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_BLACKSTONE));
            variant(blockModels, Blocks.CHISELED_STONE_BRICKS, RotatingPillarTextureType.Y,        builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_STONEBRICKS));
            variant(blockModels, Blocks.POLISHED_DIORITE, FullTextureType.INSTANCE,             builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_DIORITE));
            variant(blockModels, Blocks.POLISHED_GRANITE, FullTextureType.INSTANCE,             builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_GRANITE));
            variant(blockModels, Blocks.REDSTONE_BLOCK, OmniPillarTextureType.INSTANCE,               builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_REDSTONE));
            variant(blockModels, Blocks.SEA_LANTERN, FullTextureType.INSTANCE,                  builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_SEA_LANTERN));

            TextureMapping mapping = new TextureMapping()
                    .put(SLOT_REDSTONE, TEX_REDSTONE)
                    .put(SLOT_GLASS, TEX_GLASS)
                    .put(TextureSlot.PARTICLE, TEX_REDSTONE);
            TEMPLATE_BLOCK.extend()
                    .element(elem -> elem.allFaces((dir, face) -> face.texture(SLOT_REDSTONE).cullface(dir)).lightEmission(15))
                    .element(elem -> elem.allFaces((dir, face) -> face.texture(SLOT_GLASS).cullface(dir)))
                    .renderType("cutout")
                    .build()
                    .create(Blocks.OAK_PLANKS, mapping, blockModels.modelOutput);
            variant(blockModels, Blocks.OAK_PLANKS, FullTextureType.INSTANCE, builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_GLASS));

            ConTexBlockModelDefinitionGenerator slabGenerator = new ConTexBlockModelDefinitionGenerator(Blocks.OAK_SLAB);
            TextureMapping slabTextures = TextureMapping.column(TEX_DIORITE, TEX_DIORITE);
            Identifier bottomSlab = ModelTemplates.SLAB_BOTTOM.create(Blocks.OAK_SLAB, slabTextures, blockModels.modelOutput);
            Identifier topSlab = ModelTemplates.SLAB_TOP.create(Blocks.OAK_SLAB, slabTextures, blockModels.modelOutput);
            Identifier doubleSlab = ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.OAK_SLAB, "_double", slabTextures, blockModels.modelOutput);
            slabGenerator.variant(MultiVariantGenerator.dispatch(Blocks.OAK_SLAB).with(
                    PropertyDispatch.initial(BlockStateProperties.SLAB_TYPE)
                            .select(SlabType.BOTTOM, BlockModelGenerators.plainVariant(bottomSlab))
                            .select(SlabType.TOP, BlockModelGenerators.plainVariant(topSlab))
                            .select(SlabType.DOUBLE, BlockModelGenerators.plainVariant(doubleSlab))
            ));
            slabGenerator.metadata(FullTextureType.INSTANCE, builder -> builder.predicate(SameStatePredicate.INSTANCE).addTexture(TEX_DIORITE));
            blockModels.blockStateOutput.accept(slabGenerator);

            TexturedModel.CARPET.get(Blocks.RED_WOOL).create(Blocks.RED_CARPET, blockModels.modelOutput);
            variant(blockModels, Blocks.RED_CARPET, FullCarpetTextureType.TYPES[Direction.DOWN.ordinal()], builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_RED_WOOL));

            ConTexBlockModelDefinitionGenerator logGenerator = new ConTexBlockModelDefinitionGenerator(Blocks.OAK_LOG);
            MultiVariant logVariant = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.OAK_LOG));
            MultiVariant logVariantHor = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(Blocks.OAK_LOG, "_horizontal"));
            logGenerator.variant((MultiVariantGenerator) BlockModelGenerators.createRotatedPillarWithHorizontalVariant(Blocks.OAK_LOG, logVariant, logVariantHor));
            logGenerator.metadata(PillarTextureType.X, builder -> builder.predicate(SameStatePredicate.INSTANCE).addTexture(TEX_OAK_LOG).addStateFilter(BlockStateProperties.AXIS, Direction.Axis.X));
            logGenerator.metadata(PillarTextureType.Y, builder -> builder.predicate(SameStatePredicate.INSTANCE).addTexture(TEX_OAK_LOG).addStateFilter(BlockStateProperties.AXIS, Direction.Axis.Y));
            logGenerator.metadata(PillarTextureType.Z, builder -> builder.predicate(SameStatePredicate.INSTANCE).addTexture(TEX_OAK_LOG).addStateFilter(BlockStateProperties.AXIS, Direction.Axis.Z));
            blockModels.blockStateOutput.accept(logGenerator);

            ConTexBlockModelDefinitionGenerator stoneGenerator = new ConTexBlockModelDefinitionGenerator(Blocks.STONE);
            Variant stoneVariant = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.STONE));
            Variant stoneMirroredVariant = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(Blocks.STONE, "_mirrored"));
            stoneGenerator.variant(MultiVariantGenerator.dispatch(Blocks.STONE, BlockModelGenerators.createRotatedVariants(stoneVariant, stoneMirroredVariant)));
            stoneGenerator.metadata(FullTextureType.INSTANCE, builder -> builder.predicate(SameBlockPredicate.INSTANCE).addTexture(TEX_STONE));
            blockModels.blockStateOutput.accept(stoneGenerator);
        }

        private static void variant(BlockModelGenerators blockModels, Block block, TextureType type, UnaryOperator<MetaEntryBuilder> metaBuilder)
        {
            ConTexBlockModelDefinitionGenerator generator = new ConTexBlockModelDefinitionGenerator(block)
                    .variant(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(block))))
                    .metadata(type, metaBuilder);
            blockModels.blockStateOutput.accept(generator);
        }

        @Override
        @SuppressWarnings("NullableProblems")
        protected Stream<? extends Holder<Block>> getKnownBlocks()
        {
            return Stream.empty();
        }

        @Override
        @SuppressWarnings("NullableProblems")
        protected Stream<? extends Holder<Item>> getKnownItems()
        {
            return Stream.empty();
        }
    }

    private static final class TestSpriteSourceProvider extends SpriteSourceProvider
    {
        public TestSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
        {
            super(output, lookupProvider, Constants.MOD_ID);
        }

        @Override
        protected void gather()
        {
            atlas(AtlasIds.BLOCKS)
                    .addSource(new ConTexSpriteSource(
                            Identifier.withDefaultNamespace("block/polished_diorite"),
                            new Border(2)
                    ))
                    .addSource(new ConTexSpriteSource(
                            Identifier.withDefaultNamespace("block/polished_granite"),
                            new Border(2)
                    ))
                    .addSource(new ConTexSpriteSource(
                            Identifier.withDefaultNamespace("block/stone"),
                            new Border(2)
                    ))
                    .addSource(new ConTexSpriteSource(
                            Identifier.withDefaultNamespace("block/sea_lantern"),
                            new Border(2, true, true, false, false)
                    ))
                    .addSource(new ConTexSpriteSource(
                            Identifier.withDefaultNamespace("block/red_wool"),
                            new Border(2, false, false, true, true)
                    ));
        }
    }
}
