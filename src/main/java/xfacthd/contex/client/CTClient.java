package xfacthd.contex.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import xfacthd.contex.api.type.RegisterTextureMetaEvent;
import xfacthd.contex.api.utils.Builtin;
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
        event.registerType(Builtin.Types.SIMPLE, SimpleTextureType.INSTANCE);
        event.registerType(Builtin.Types.FULL, FullTextureType.INSTANCE);
        event.registerType(Builtin.Types.PILLAR_X, PillarTextureType.X);
        event.registerType(Builtin.Types.PILLAR_Y, PillarTextureType.Y);
        event.registerType(Builtin.Types.PILLAR_Z, PillarTextureType.Z);
        event.registerType(Builtin.Types.PILLAR_OMNI, OmniPillarTextureType.INSTANCE);
        event.registerType(Builtin.Types.CARPET_SIMPLE, SimpleCarpetTextureType.Y);
        event.registerType(Builtin.Types.CARPET_SIMPLE_X, SimpleCarpetTextureType.X);
        event.registerType(Builtin.Types.CARPET_SIMPLE_Z, SimpleCarpetTextureType.Z);
        event.registerType(Builtin.Types.CARPET_FULL, FullCarpetTextureType.Y);
        event.registerType(Builtin.Types.CARPET_FULL_X, FullCarpetTextureType.X);
        event.registerType(Builtin.Types.CARPET_FULL_Z, FullCarpetTextureType.Z);

        event.registerPredicate(Builtin.Predicates.SAME_BLOCK, SameBlockPredicate.CODEC);
        event.registerPredicate(Builtin.Predicates.SAME_STATE, SameStatePredicate.CODEC);
    }
}
