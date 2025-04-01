package xfacthd.contex.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import xfacthd.contex.api.type.RegisterTextureMetaEvent;
import xfacthd.contex.api.utils.Constants;
import xfacthd.contex.api.utils.Utils;
import xfacthd.contex.client.data.MetadataRegistry;
import xfacthd.contex.client.model.ConTexBlockModelDefinition;
import xfacthd.contex.client.predicate.SameBlockPredicate;
import xfacthd.contex.client.predicate.SameStatePredicate;
import xfacthd.contex.client.type.FullCarpetTextureType;
import xfacthd.contex.client.type.FullTextureType;
import xfacthd.contex.client.type.OmniPillarTextureType;
import xfacthd.contex.client.type.PillarTextureType;
import xfacthd.contex.client.type.SimpleCarpetTextureType;
import xfacthd.contex.client.type.SimpleTextureType;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public final class CTClient
{
    public CTClient(IEventBus modBus)
    {
        modBus.addListener(CTClient::onRegisterBlockStateModels);
        modBus.addListener(CTClient::onInitClientRegistries);
        modBus.addListener(CTClient::onRegisterMetadata);
    }

    private static void onRegisterBlockStateModels(final RegisterBlockStateModels event)
    {
        event.registerDefinition(Utils.rl("definition"), ConTexBlockModelDefinition.CODEC);
    }

    private static void onInitClientRegistries(final InitializeClientRegistriesEvent event)
    {
        MetadataRegistry.init();
    }

    private static void onRegisterMetadata(final RegisterTextureMetaEvent event)
    {
        event.registerType(Utils.rl("simple"), SimpleTextureType.INSTANCE);
        event.registerType(Utils.rl("full"), FullTextureType.INSTANCE);
        event.registerType(Utils.rl("pillar_x"), PillarTextureType.X);
        event.registerType(Utils.rl("pillar_y"), PillarTextureType.Y);
        event.registerType(Utils.rl("pillar_z"), PillarTextureType.Z);
        event.registerType(Utils.rl("pillar_omni"), OmniPillarTextureType.INSTANCE);
        event.registerType(Utils.rl("carpet_simple"), SimpleCarpetTextureType.Y);
        event.registerType(Utils.rl("carpet_simple_x"), SimpleCarpetTextureType.X);
        event.registerType(Utils.rl("carpet_simple_z"), SimpleCarpetTextureType.Z);
        event.registerType(Utils.rl("carpet_full"), FullCarpetTextureType.Y);
        event.registerType(Utils.rl("carpet_full_x"), FullCarpetTextureType.X);
        event.registerType(Utils.rl("carpet_full_z"), FullCarpetTextureType.Z);

        event.registerPredicate(Utils.rl("same_block"), SameBlockPredicate.CODEC);
        event.registerPredicate(Utils.rl("same_state"), SameStatePredicate.CODEC);
    }
}
