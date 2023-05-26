package xfacthd.contex.client.data;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.type.TextureType;
import xfacthd.contex.api.type.ConnectionPredicate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class MetadataRegistry
{
    private static final Map<ResourceLocation, TextureType> TYPES = new HashMap<>();
    private static final Map<ResourceLocation, ConnectionPredicate> PREDICATES = new HashMap<>();
    private static boolean locked = false;

    public static synchronized void registerType(ResourceLocation name, TextureType type)
    {
        checkNotLocked();
        TextureType oldType = TYPES.put(name, type);
        if (oldType != null)
        {
            throw new IllegalStateException("Duplicate TextureType registered: " + name);
        }
    }

    public static synchronized void registerPredicate(ResourceLocation name, ConnectionPredicate predicate)
    {
        checkNotLocked();
        ConnectionPredicate oldPred = PREDICATES.put(name, predicate);
        if (oldPred != null)
        {
            throw new IllegalStateException("Duplicate TextureType registered: " + name);
        }
    }

    public static TextureType getType(ResourceLocation name, Supplier<RuntimeException> excSup)
    {
        TextureType type = TYPES.get(name);
        if (type == null)
        {
            throw excSup.get();
        }
        return type;
    }

    public static ConnectionPredicate getPredicate(ResourceLocation name, Supplier<RuntimeException> excSup)
    {
        ConnectionPredicate predicate = PREDICATES.get(name);
        if (predicate == null)
        {
            throw excSup.get();
        }
        return predicate;
    }

    public static void setLocked(boolean locked)
    {
        MetadataRegistry.locked = locked;
    }

    public static void clear()
    {
        checkNotLocked();
        TYPES.clear();
        PREDICATES.clear();
    }

    private static void checkNotLocked()
    {
        Preconditions.checkState(!locked, "Metadata registry is locked!");
    }
}
