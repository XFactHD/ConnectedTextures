package xfacthd.contex;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xfacthd.contex.api.model.ConTexLoaderBuilder;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Constants;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TestDataGeneratorHandler
{
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event)
    {
        event.getGenerator().addProvider(
                event.includeClient(),
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
                    .connectedTexture(TEX_DEEPSLATE, Builtin.Types.PILLAR_X)
                    .optional();

            cubeAll(TEX_BLACKSTONE.toString(), TEX_BLACKSTONE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .connectedTexture(TEX_BLACKSTONE, Builtin.Types.PILLAR_Z)
                    .optional();

            cubeAll(TEX_STONEBRICKS.toString(), TEX_STONEBRICKS)
                    .customLoader(ConTexLoaderBuilder::new)
                    .connectedTexture(TEX_STONEBRICKS, Builtin.Types.PILLAR_Y)
                    .optional();

            cubeAll(TEX_GLASS.toString(), TEX_GLASS)
                    .customLoader(ConTexLoaderBuilder::new)
                    .connectedTexture(TEX_GLASS, Builtin.Types.FULL)
                    .optional();

            cubeAll(TEX_DIORITE.toString(), TEX_DIORITE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .connectedTexture(TEX_DIORITE, Builtin.Types.FULL)
                    .optional();

            cubeAll(TEX_GRANITE.toString(), TEX_GRANITE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .connectedTexture(TEX_GRANITE, Builtin.Types.FULL)
                    .optional();

            cubeAll(TEX_REDSTONE.toString(), TEX_REDSTONE)
                    .customLoader(ConTexLoaderBuilder::new)
                    .connectedTexture(TEX_REDSTONE, Builtin.Types.PILLAR_OMNI)
                    .optional();
        }
    }
}
