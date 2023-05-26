package xfacthd.contex.client;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import xfacthd.contex.api.type.RegisterTextureMetaEvent;
import xfacthd.contex.api.utils.Constants;
import xfacthd.contex.api.utils.Utils;
import xfacthd.contex.client.data.MetadataRegistry;
import xfacthd.contex.client.loader.ConTexLoader;
import xfacthd.contex.client.predicate.SameBlockPredicate;
import xfacthd.contex.client.predicate.SameStatePredicate;
import xfacthd.contex.client.type.*;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CTClient
{
    @SubscribeEvent
    public static void onRegisterGeometryLoader(final ModelEvent.RegisterGeometryLoaders event)
    {
        MetadataRegistry.setLocked(false);
        MetadataRegistry.clear();
        ModLoader.get().postEvent(new RegisterTextureMetaEvent(MetadataRegistry::registerType, MetadataRegistry::registerPredicate));
        MetadataRegistry.setLocked(true);

        event.register("loader", new ConTexLoader());
    }

    @SubscribeEvent
    public static void onRegisterMetadata(final RegisterTextureMetaEvent event)
    {
        event.registerType(Utils.rl("simple"), new SimpleTextureType());
        event.registerType(Utils.rl("full"), new FullTextureType());
        event.registerType(Utils.rl("pillar_x"), new PillarTextureType(Direction.Axis.X));
        event.registerType(Utils.rl("pillar_y"), new PillarTextureType(Direction.Axis.Y));
        event.registerType(Utils.rl("pillar_z"), new PillarTextureType(Direction.Axis.Z));
        event.registerType(Utils.rl("pillar_omni"), new OmniPillarTextureType());

        event.registerPredicate(Constants.DEFAULT_PREDICATE, new SameBlockPredicate());
        event.registerPredicate(Utils.rl("same_state"), new SameStatePredicate());
    }
}
