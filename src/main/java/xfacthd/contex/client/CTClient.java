package xfacthd.contex.client;

import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import xfacthd.contex.api.type.RegisterTextureMetaEvent;
import xfacthd.contex.api.utils.Builtin;
import xfacthd.contex.api.utils.Constants;
import xfacthd.contex.client.data.MetadataRegistry;
import xfacthd.contex.client.loader.ConTexLoader;
import xfacthd.contex.client.predicate.SameBlockPredicate;
import xfacthd.contex.client.predicate.SameStatePredicate;
import xfacthd.contex.client.type.*;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CTClient
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
        event.registerType(Builtin.Types.SIMPLE, new SimpleTextureType());
        event.registerType(Builtin.Types.FULL, new FullTextureType());
        event.registerType(Builtin.Types.PILLAR_X, new PillarTextureType(Direction.Axis.X));
        event.registerType(Builtin.Types.PILLAR_Y, new PillarTextureType(Direction.Axis.Y));
        event.registerType(Builtin.Types.PILLAR_Z, new PillarTextureType(Direction.Axis.Z));
        event.registerType(Builtin.Types.PILLAR_OMNI, new OmniPillarTextureType());

        event.registerPredicate(Builtin.Predicates.SAME_BLOCK, new SameBlockPredicate());
        event.registerPredicate(Builtin.Predicates.SAME_STATE, new SameStatePredicate());
    }



    private CTClient() { }
}
