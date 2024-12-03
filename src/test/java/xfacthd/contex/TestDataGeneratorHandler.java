package xfacthd.contex;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xfacthd.contex.api.model.builder.ConTexLoaderBuilder;
import xfacthd.contex.api.type.OcclusionMode;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Constants;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TestDataGeneratorHandler
{
    private TestDataGeneratorHandler() { }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent.Client event)
    {
        event.getGenerator().addProvider(
                true,
                new TestBlockModelProvider(
                        event.getGenerator().getPackOutput(),
                        event.getExistingFileHelper()
                )
        );
    }



    private static final class TestBlockModelProvider extends BlockModelProvider
    {
        private final ResourceLocation TEX_DEEPSLATE = mcLoc("block/chiseled_deepslate");
        private final ResourceLocation TEX_BLACKSTONE = mcLoc("block/chiseled_polished_blackstone");
        private final ResourceLocation TEX_STONEBRICKS = mcLoc("block/chiseled_stone_bricks");
        private final ResourceLocation TEX_GLASS = mcLoc("block/glass");
        private final ResourceLocation TEX_DIORITE = mcLoc("block/polished_diorite");
        private final ResourceLocation TEX_GRANITE = mcLoc("block/polished_granite");
        private final ResourceLocation TEX_REDSTONE = mcLoc("block/redstone_block");

        public TestBlockModelProvider(PackOutput output, ExistingFileHelper fileHelper)
        {
            super(output, Constants.MOD_ID, fileHelper);
        }

        @Override
        protected void registerModels()
        {
            cubeAll(TEX_DEEPSLATE.toString(), TEX_DEEPSLATE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .addCtEntry(Builtin.Types.PILLAR_X, TEX_DEEPSLATE)
                    .optional();

            cubeAll(TEX_BLACKSTONE.toString(), TEX_BLACKSTONE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .addCtEntry(Builtin.Types.PILLAR_Z, TEX_BLACKSTONE)
                    .optional();

            cubeAll(TEX_STONEBRICKS.toString(), TEX_STONEBRICKS)
                    .customLoader(ConTexLoaderBuilder::new)
                    .addCtEntry(Builtin.Types.PILLAR_Y, TEX_STONEBRICKS)
                    .optional();

            cubeAll(TEX_GLASS.toString(), TEX_GLASS)
                    .customLoader(ConTexLoaderBuilder::new)
                    .addCtEntry(Builtin.Types.FULL, e -> e.addTexture(TEX_GLASS).occlusionMode(OcclusionMode.SOLID_OR_SELF))
                    .optional();

            cubeAll(TEX_DIORITE.toString(), TEX_DIORITE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .addCtEntry(Builtin.Types.FULL, TEX_DIORITE)
                    .optional();

            cubeAll(TEX_GRANITE.toString(), TEX_GRANITE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .addCtEntry(Builtin.Types.FULL, TEX_GRANITE)
                    .optional();

            cubeAll(TEX_REDSTONE.toString(), TEX_REDSTONE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .addCtEntry(Builtin.Types.PILLAR_OMNI, TEX_REDSTONE)
                    .optional();

            withExistingParent("minecraft:block/oak_planks", "block/block")
                    .customLoader(ConTexLoaderBuilder::new)
                        .addCtEntry(Builtin.Types.FULL, TEX_GLASS)
                        .optional()
                        .end()
                    .element()
                        .allFaces((dir, face) -> face.texture("#redstone").cullface(dir).emissivity(15, 15))
                        .end()
                    .element()
                        .allFaces((dir, face) -> face.texture("#glass").cullface(dir))
                        .end()
                    .texture("redstone", TEX_REDSTONE)
                    .texture("glass", TEX_GLASS)
                    .texture("particle", TEX_REDSTONE)
                    .renderType("cutout");
        }
    }
}
