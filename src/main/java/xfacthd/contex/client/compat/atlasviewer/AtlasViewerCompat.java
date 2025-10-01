package xfacthd.contex.client.compat.atlasviewer;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.contex.api.texture.Border;
import xfacthd.contex.api.texture.ConTexSpriteSource;
import xfacthd.contex.client.texture.ConTexSpriteSupplier;

public final class AtlasViewerCompat
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component LABEL_TEXTURE = Component.translatable("label.contex.source_tooltip.ctm.texture");
    private static final Component LABEL_BORDER = Component.translatable("label.contex.source_tooltip.ctm.border");
    private static final Component LABEL_SPRITE = Component.translatable("label.contex.source_tooltip.ctm.sprite");
    private static final Component LABEL_MIRROR_PARALLEL = Component.translatable("label.contex.source_tooltip.ctm.mirror_parallel");
    private static final Component LABEL_MIRROR_PERPENDICULAR = Component.translatable("label.contex.source_tooltip.ctm.mirror_perpendicular");
    private static final String VALUE_BORDER = "value.contex.source_tooltip.ctm.border";

    public static void init(IEventBus modBus)
    {
        if (ModList.get().isLoaded("atlasviewer"))
        {
            try
            {
                GuardedClientAccess.init(modBus);
            }
            catch (Throwable e)
            {
                LOGGER.error("Failed to initialize AtlasViewer compat", e);
            }
        }
    }

    private static final class GuardedClientAccess
    {
        public static void init(IEventBus modBus)
        {
            modBus.addListener(GuardedClientAccess::onRegisterSpriteSourceDetails);
        }

        private static void onRegisterSpriteSourceDetails(final RegisterSpriteSourceDetailsEvent event)
        {
            event.registerPrimaryResourceGetter(ConTexSpriteSupplier.class, ConTexSpriteSupplier::imgResource);

            event.registerSourceTooltipAppender(ConTexSpriteSource.class, (src, consumer) ->
            {
                ResourceLocation resource = src.texture();
                ResourceLocation sprite = src.sprite().orElse(resource);
                Border border = src.border();

                consumer.accept(AtlasViewerCompat.LABEL_TEXTURE, Component.literal(resource.toString()));
                consumer.accept(AtlasViewerCompat.LABEL_BORDER, Component.translatable(
                        AtlasViewerCompat.VALUE_BORDER,
                        border.left(),
                        border.right(),
                        border.top(),
                        border.bottom()
                ));
                consumer.accept(AtlasViewerCompat.LABEL_MIRROR_PARALLEL, Component.literal(Boolean.toString(border.mirrorParallel())));
                consumer.accept(AtlasViewerCompat.LABEL_MIRROR_PERPENDICULAR, Component.literal(Boolean.toString(border.mirrorPerpendicular())));
                consumer.accept(AtlasViewerCompat.LABEL_SPRITE, Component.literal(sprite.toString()));
            });
        }

        private GuardedClientAccess() { }
    }

    private AtlasViewerCompat() { }
}
