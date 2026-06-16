package io.github.xfacthd.contex.client.compat.atlasviewer;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import io.github.xfacthd.contex.api.texture.Border;
import io.github.xfacthd.contex.api.texture.ConTexSpriteSource;
import io.github.xfacthd.contex.client.texture.ConTexCompactSpriteSupplier;
import io.github.xfacthd.contex.client.texture.ConTexFullSpriteSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

public final class AtlasViewerCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component LABEL_TEXTURE = Component.translatable("label.contex.source_tooltip.ctm.texture");
    private static final Component LABEL_BORDER = Component.translatable("label.contex.source_tooltip.ctm.border");
    private static final Component LABEL_MIRROR_PARALLEL = Component.translatable("label.contex.source_tooltip.ctm.mirror_parallel");
    private static final Component LABEL_MIRROR_PERPENDICULAR = Component.translatable("label.contex.source_tooltip.ctm.mirror_perpendicular");
    private static final String VALUE_BORDER = "value.contex.source_tooltip.ctm.border";

    public static void init(IEventBus modBus) {
        if (ModList.get().isLoaded("atlasviewer")) {
            try {
                GuardedClientAccess.init(modBus);
            } catch (Throwable e) {
                LOGGER.error("Failed to initialize AtlasViewer compat", e);
            }
        }
    }

    private static final class GuardedClientAccess {
        public static void init(IEventBus modBus) {
            modBus.addListener(GuardedClientAccess::onRegisterSpriteSourceDetails);
        }

        private static void onRegisterSpriteSourceDetails(final RegisterSpriteSourceDetailsEvent event) {
            event.registerPrimaryResourceGetter(ConTexCompactSpriteSupplier.class, ConTexCompactSpriteSupplier::imgResource);
            event.registerPrimaryResourceGetter(ConTexFullSpriteSupplier.class, ConTexFullSpriteSupplier::imgResource);

            event.registerSourceTooltipAppender(ConTexSpriteSource.class, (src, consumer) -> {
                Identifier resource = src.texture();
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
            });
        }

        private GuardedClientAccess() { }
    }

    private AtlasViewerCompat() { }
}
