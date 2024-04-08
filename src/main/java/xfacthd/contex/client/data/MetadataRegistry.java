package xfacthd.contex.client.data;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoader;
import xfacthd.contex.api.type.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MetadataRegistry
{
    private static final Map<ResourceLocation, TextureType> TYPES = new HashMap<>();
    private static final Map<ResourceLocation, ConnectionPredicate> PREDICATES = new HashMap<>();

    public static void init()
    {
        ModLoader.get().postEvent(new RegisterTextureMetaEvent(
                MetadataRegistry::registerType, MetadataRegistry::registerPredicate
        ));
    }

    private static void registerType(ResourceLocation name, TextureType type)
    {
        TextureType oldType = TYPES.put(name, type);
        if (oldType != null)
        {
            throw new IllegalStateException("Duplicate TextureType registered: " + name);
        }
    }

    private static void registerPredicate(ResourceLocation name, ConnectionPredicate predicate)
    {
        ConnectionPredicate oldPred = PREDICATES.put(name, predicate);
        if (oldPred != null)
        {
            throw new IllegalStateException("Duplicate TextureType registered: " + name);
        }
    }

    public static TextureType getType(ResourceLocation name, Function<ResourceLocation, RuntimeException> excSup)
    {
        TextureType type = TYPES.get(name);
        if (type == null)
        {
            throw excSup.apply(name);
        }
        return type;
    }

    public static ConnectionPredicate getPredicate(ResourceLocation name, Function<ResourceLocation, RuntimeException> excSup)
    {
        ConnectionPredicate predicate = PREDICATES.get(name);
        if (predicate == null)
        {
            throw excSup.apply(name);
        }
        return predicate;
    }



    private MetadataRegistry() { }
}
