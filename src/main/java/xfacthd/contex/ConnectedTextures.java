package xfacthd.contex;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import xfacthd.contex.api.utils.Constants;

@Mod(Constants.MOD_ID)
public class ConnectedTextures
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public ConnectedTextures()
    {
        if (FMLEnvironment.dist.isDedicatedServer())
        {
            LOGGER.warn("ConnectedTextures is a client-only mod, it does nothing on a server and can be safely removed from the server");
        }
    }
}
