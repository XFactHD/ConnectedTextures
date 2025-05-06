package xfacthd.contex.client;

import net.minecraft.core.Direction;
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
import xfacthd.contex.client.predicate.MatchBlockPredicate;
import xfacthd.contex.client.predicate.MatchStatePredicate;
import xfacthd.contex.client.predicate.MatchTagPredicate;
import xfacthd.contex.client.predicate.SameBlockPredicate;
import xfacthd.contex.client.predicate.SameStatePredicate;
import xfacthd.contex.client.type.FullCarpetTextureType;
import xfacthd.contex.client.type.FullTextureType;
import xfacthd.contex.client.type.OmniPillarTextureType;
import xfacthd.contex.client.type.PillarTextureType;
import xfacthd.contex.client.type.RotatingPillarTextureType;
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
        event.registerType(Utils.rl("pillar_rot_x"), RotatingPillarTextureType.X);
        event.registerType(Utils.rl("pillar_rot_y"), RotatingPillarTextureType.Y);
        event.registerType(Utils.rl("pillar_rot_z"), RotatingPillarTextureType.Z);
        event.registerType(Utils.rl("pillar_omni"), OmniPillarTextureType.INSTANCE);
        event.registerType(Utils.rl("carpet_simple"), SimpleCarpetTextureType.TYPES[Direction.DOWN.ordinal()]);
        event.registerType(Utils.rl("carpet_simple_west"), SimpleCarpetTextureType.TYPES[Direction.WEST.ordinal()]);
        event.registerType(Utils.rl("carpet_simple_east"), SimpleCarpetTextureType.TYPES[Direction.EAST.ordinal()]);
        event.registerType(Utils.rl("carpet_simple_north"), SimpleCarpetTextureType.TYPES[Direction.NORTH.ordinal()]);
        event.registerType(Utils.rl("carpet_simple_south"), SimpleCarpetTextureType.TYPES[Direction.SOUTH.ordinal()]);
        event.registerType(Utils.rl("carpet_simple_up"), SimpleCarpetTextureType.TYPES[Direction.UP.ordinal()]);
        event.registerType(Utils.rl("carpet_full"), FullCarpetTextureType.TYPES[Direction.DOWN.ordinal()]);
        event.registerType(Utils.rl("carpet_full_west"), FullCarpetTextureType.TYPES[Direction.WEST.ordinal()]);
        event.registerType(Utils.rl("carpet_full_east"), FullCarpetTextureType.TYPES[Direction.EAST.ordinal()]);
        event.registerType(Utils.rl("carpet_full_north"), FullCarpetTextureType.TYPES[Direction.NORTH.ordinal()]);
        event.registerType(Utils.rl("carpet_full_south"), FullCarpetTextureType.TYPES[Direction.SOUTH.ordinal()]);
        event.registerType(Utils.rl("carpet_full_up"), FullCarpetTextureType.TYPES[Direction.UP.ordinal()]);

        event.registerPredicate(Utils.rl("same_block"), SameBlockPredicate.CODEC);
        event.registerPredicate(Utils.rl("same_state"), SameStatePredicate.CODEC);
        event.registerPredicate(Utils.rl("match_block"), MatchBlockPredicate.CODEC);
        event.registerPredicate(Utils.rl("match_state"), MatchStatePredicate.CODEC);
        event.registerPredicate(Utils.rl("match_tag"), MatchTagPredicate.CODEC);
    }
}
