package io.github.xfacthd.contex;

import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourcesEvent;
import net.neoforged.neoforge.common.NeoForge;
import io.github.xfacthd.contex.api.type.RegisterTextureMetaEvent;
import io.github.xfacthd.contex.api.utils.Constants;
import io.github.xfacthd.contex.api.utils.Utils;
import io.github.xfacthd.contex.client.compat.CompatHandler;
import io.github.xfacthd.contex.client.data.MetadataRegistry;
import io.github.xfacthd.contex.client.model.ConTexBlockModelDefinition;
import io.github.xfacthd.contex.client.predicate.MatchBlockPredicate;
import io.github.xfacthd.contex.client.predicate.MatchStatePredicate;
import io.github.xfacthd.contex.client.predicate.MatchTagPredicate;
import io.github.xfacthd.contex.client.predicate.SameBlockPredicate;
import io.github.xfacthd.contex.client.predicate.SameStatePredicate;
import io.github.xfacthd.contex.api.texture.ConTexSpriteSource;
import io.github.xfacthd.contex.client.type.FullCarpetTextureType;
import io.github.xfacthd.contex.client.type.FullTextureType;
import io.github.xfacthd.contex.client.type.OmniPillarTextureType;
import io.github.xfacthd.contex.client.type.PillarTextureType;
import io.github.xfacthd.contex.client.type.RotatingPillarTextureType;
import io.github.xfacthd.contex.client.type.SimpleCarpetTextureType;
import io.github.xfacthd.contex.client.type.SimpleTextureType;
import io.github.xfacthd.contex.client.util.ConTexCommand;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public final class ConnectedTextures
{
    public ConnectedTextures(IEventBus modBus)
    {
        modBus.addListener(ConnectedTextures::onRegisterBlockStateModels);
        modBus.addListener(ConnectedTextures::onRegisterSpriteSources);
        modBus.addListener(ConnectedTextures::onInitClientRegistries);
        modBus.addListener(ConnectedTextures::onRegisterMetadata);

        NeoForge.EVENT_BUS.addListener(ConnectedTextures::onRegisterClientCommands);

        CompatHandler.init(modBus);
    }

    private static void onRegisterBlockStateModels(final RegisterBlockStateModels event)
    {
        event.registerDefinition(Utils.rl("definition"), ConTexBlockModelDefinition.CODEC);
    }

    private static void onRegisterSpriteSources(final RegisterSpriteSourcesEvent event)
    {
        event.register(Utils.rl("ctm"), ConTexSpriteSource.CODEC);
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

    private static void onRegisterClientCommands(final RegisterClientCommandsEvent event)
    {
        ConTexCommand.register(event.getDispatcher());
    }
}
