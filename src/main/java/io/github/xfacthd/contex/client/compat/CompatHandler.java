package io.github.xfacthd.contex.client.compat;

import net.neoforged.bus.api.IEventBus;
import io.github.xfacthd.contex.client.compat.atlasviewer.AtlasViewerCompat;

public final class CompatHandler
{
    public static void init(IEventBus modBus)
    {
        AtlasViewerCompat.init(modBus);
    }

    private CompatHandler() { }
}
