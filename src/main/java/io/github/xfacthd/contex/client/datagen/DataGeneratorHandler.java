package io.github.xfacthd.contex.client.datagen;

import io.github.xfacthd.contex.ConnectedTextures;
import io.github.xfacthd.contex.api.datagen.ConTexBlockModelDefinitionGenerator;
import io.github.xfacthd.contex.api.texture.Border;
import io.github.xfacthd.contex.api.texture.ConTexSpriteSource;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import io.github.xfacthd.contex.api.utils.Constants;
import io.github.xfacthd.contex.client.predicate.SameBlockPredicate;
import io.github.xfacthd.contex.client.type.FullTextureType;
import net.minecraft.SharedConstants;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColorCollection;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public final class DataGeneratorHandler {
    public DataGeneratorHandler(IEventBus modBus) {
        modBus.addListener(DataGeneratorHandler::onGatherData);
    }

    private static void onGatherData(final GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        DataGenerator.PackGenerator packGen = generator.getPackGenerator(true, "builtin_glass_ct", ConnectedTextures.BUILTIN_RP_ID.getPath());
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getReloadableLookupProvider();

        packGen.addProvider(output -> new PackMetadataGenerator(output).add(
                PackMetadataSection.CLIENT_TYPE,
                new PackMetadataSection(
                        ConnectedTextures.BUILTIN_RP_DESC,
                        SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange()
                )
        ));
        packGen.addProvider(BuiltinCtBlockModelProvider::new);
        packGen.addProvider(output -> new BuiltinCtSpriteSourceProvider(output, lookupProvider));
    }

    private static final class BuiltinCtBlockModelProvider extends ModelProvider {
        public BuiltinCtBlockModelProvider(PackOutput output) {
            super(output, Constants.MOD_ID);
        }

        @Override
        protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
            generateGlassBlock(blockModels, Blocks.GLASS, mcLocation("block/glass"));
            ColorCollection.zipApply(BlockItemIds.STAINED_GLASS.map(BlockItemId::block), Blocks.STAINED_GLASS, (key, block) ->
                    generateGlassBlock(blockModels, block, key.identifier().withPrefix("block/"))
            );
        }

        private static void generateGlassBlock(BlockModelGenerators blockModels, Block block, Identifier texture) {
            ConTexBlockModelDefinitionGenerator generator = new ConTexBlockModelDefinitionGenerator(block)
                    .variant(MultiVariantGenerator.dispatch(
                            block,
                            BlockModelGenerators.plainVariant(
                                    ModelLocationUtils.getModelLocation(block)
                            )
                    ))
                    .metadata(FullTextureType.INSTANCE, builder ->
                            builder.predicate(SameBlockPredicate.INSTANCE)
                                    .occlusionMode(OcclusionMode.SOLID_OR_SELF)
                                    .addTexture(texture)
                    );
            blockModels.blockStateOutput.accept(generator);
        }
    }

    private static final class BuiltinCtSpriteSourceProvider extends SpriteSourceProvider {
        public BuiltinCtSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, Constants.MOD_ID);
        }

        @Override
        protected void gather() {
            SourceList blockAtlas = atlas(AtlasIds.BLOCKS);
            blockAtlas.addSource(new ConTexSpriteSource(
                            Identifier.withDefaultNamespace("block/glass"),
                            new Border(1)
                    ));
            BlockItemIds.STAINED_GLASS.forEach(id ->
                    blockAtlas.addSource(new ConTexSpriteSource(
                            id.block().identifier().withPrefix("block/"),
                            new Border(1)
                    ))
            );
        }
    }
}
