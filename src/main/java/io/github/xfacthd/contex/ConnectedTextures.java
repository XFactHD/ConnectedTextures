package io.github.xfacthd.contex;

import io.github.xfacthd.contex.api.texture.ConTexSpriteSource;
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
import io.github.xfacthd.contex.client.strategy.CompactTextureStrategy;
import io.github.xfacthd.contex.client.strategy.FullTextureStrategy;
import io.github.xfacthd.contex.client.type.FullCarpetTextureType;
import io.github.xfacthd.contex.client.type.FullTextureType;
import io.github.xfacthd.contex.client.type.OmniPillarTextureType;
import io.github.xfacthd.contex.client.type.PillarTextureType;
import io.github.xfacthd.contex.client.type.RotatingPillarTextureType;
import io.github.xfacthd.contex.client.type.SimpleCarpetTextureType;
import io.github.xfacthd.contex.client.type.SimpleTextureType;
import io.github.xfacthd.contex.client.util.ConTexCommand;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.InitializeClientRegistriesEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourcesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
@SuppressWarnings("UtilityClassWithPublicConstructor")
public final class ConnectedTextures {
    public static final Identifier BUILTIN_RP_ID = Utils.id("builtin_glass_ct");
    public static final Component BUILTIN_RP_DESC = Component.literal("ConTex Built-In Connected Glass");

    public ConnectedTextures(IEventBus modBus) {
        modBus.addListener(ConnectedTextures::onRegisterBlockStateModels);
        modBus.addListener(ConnectedTextures::onRegisterSpriteSources);
        modBus.addListener(ConnectedTextures::onInitClientRegistries);
        modBus.addListener(ConnectedTextures::onRegisterMetadata);
        modBus.addListener(ConnectedTextures::onAddPackFinders);

        NeoForge.EVENT_BUS.addListener(ConnectedTextures::onRegisterClientCommands);

        CompatHandler.init(modBus);
    }

    private static void onRegisterBlockStateModels(RegisterBlockStateModels event) {
        event.registerDefinition(Utils.id("definition"), ConTexBlockModelDefinition.CODEC);
    }

    private static void onRegisterSpriteSources(RegisterSpriteSourcesEvent event) {
        event.register(Utils.id("ctm"), ConTexSpriteSource.CODEC);
    }

    private static void onInitClientRegistries(InitializeClientRegistriesEvent event) {
        MetadataRegistry.init();
    }

    private static void onRegisterMetadata(RegisterTextureMetaEvent event) {
        event.registerType(Utils.id("simple"), SimpleTextureType.INSTANCE);
        event.registerType(Utils.id("full"), FullTextureType.INSTANCE);
        event.registerType(Utils.id("pillar_x"), PillarTextureType.X);
        event.registerType(Utils.id("pillar_y"), PillarTextureType.Y);
        event.registerType(Utils.id("pillar_z"), PillarTextureType.Z);
        event.registerType(Utils.id("pillar_rot_x"), RotatingPillarTextureType.X);
        event.registerType(Utils.id("pillar_rot_y"), RotatingPillarTextureType.Y);
        event.registerType(Utils.id("pillar_rot_z"), RotatingPillarTextureType.Z);
        event.registerType(Utils.id("pillar_omni"), OmniPillarTextureType.INSTANCE);
        event.registerType(Utils.id("carpet_simple"), SimpleCarpetTextureType.TYPES[Direction.DOWN.ordinal()]);
        event.registerType(Utils.id("carpet_simple_west"), SimpleCarpetTextureType.TYPES[Direction.WEST.ordinal()]);
        event.registerType(Utils.id("carpet_simple_east"), SimpleCarpetTextureType.TYPES[Direction.EAST.ordinal()]);
        event.registerType(Utils.id("carpet_simple_north"), SimpleCarpetTextureType.TYPES[Direction.NORTH.ordinal()]);
        event.registerType(Utils.id("carpet_simple_south"), SimpleCarpetTextureType.TYPES[Direction.SOUTH.ordinal()]);
        event.registerType(Utils.id("carpet_simple_up"), SimpleCarpetTextureType.TYPES[Direction.UP.ordinal()]);
        event.registerType(Utils.id("carpet_full"), FullCarpetTextureType.TYPES[Direction.DOWN.ordinal()]);
        event.registerType(Utils.id("carpet_full_west"), FullCarpetTextureType.TYPES[Direction.WEST.ordinal()]);
        event.registerType(Utils.id("carpet_full_east"), FullCarpetTextureType.TYPES[Direction.EAST.ordinal()]);
        event.registerType(Utils.id("carpet_full_north"), FullCarpetTextureType.TYPES[Direction.NORTH.ordinal()]);
        event.registerType(Utils.id("carpet_full_south"), FullCarpetTextureType.TYPES[Direction.SOUTH.ordinal()]);
        event.registerType(Utils.id("carpet_full_up"), FullCarpetTextureType.TYPES[Direction.UP.ordinal()]);

        event.registerPredicate(Utils.id("same_block"), SameBlockPredicate.CODEC);
        event.registerPredicate(Utils.id("same_state"), SameStatePredicate.CODEC);
        event.registerPredicate(Utils.id("match_block"), MatchBlockPredicate.CODEC);
        event.registerPredicate(Utils.id("match_state"), MatchStatePredicate.CODEC);
        event.registerPredicate(Utils.id("match_tag"), MatchTagPredicate.CODEC);

        event.registerStrategy(Utils.id("compact"), CompactTextureStrategy.INSTANCE);
        event.registerStrategy(Utils.id("full"), FullTextureStrategy.INSTANCE);
    }

    private static void onAddPackFinders(AddPackFindersEvent event) {
        boolean forceEnable = !FMLLoader.getCurrent().isProduction() && Boolean.getBoolean("contex.force_builtin");
        event.addPackFinders(
                BUILTIN_RP_ID,
                PackType.CLIENT_RESOURCES,
                BUILTIN_RP_DESC,
                PackSource.DEFAULT,
                forceEnable,
                Pack.Position.TOP
        );
    }

    private static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ConTexCommand.register(event.getDispatcher());
    }
}
