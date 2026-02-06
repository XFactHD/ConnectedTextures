package io.github.xfacthd.contex.client.compat;

import io.github.xfacthd.contex.client.compat.atlasviewer.AtlasViewerCompat;
import net.neoforged.bus.api.IEventBus;

public final class CompatHandler
{
    public static void init(IEventBus modBus)
    {
        AtlasViewerCompat.init(modBus);
    }

    private CompatHandler() { }
}
