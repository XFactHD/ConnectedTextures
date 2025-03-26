package xfacthd.contex.api.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterTextureMetaEvent extends Event implements IModBusEvent
{
    private final Registrar<TextureType> typeRegistrar;
    private final Registrar<MapCodec<? extends ConnectionPredicate>> predicateRegistrar;

    @ApiStatus.Internal
    public RegisterTextureMetaEvent(Registrar<TextureType> typeRegistrar, Registrar<MapCodec<? extends ConnectionPredicate>> predicateRegistrar)
    {
        this.typeRegistrar = typeRegistrar;
        this.predicateRegistrar = predicateRegistrar;
    }

    public void registerType(ResourceLocation name, TextureType type)
    {
        typeRegistrar.accept(name, type);
    }

    public void registerPredicate(ResourceLocation name, MapCodec<? extends ConnectionPredicate> predicate)
    {
        predicateRegistrar.accept(name, predicate);
    }



    public interface Registrar<T> extends BiConsumer<ResourceLocation, T>
    {
        @Override
        void accept(ResourceLocation name, T t);
    }
}
