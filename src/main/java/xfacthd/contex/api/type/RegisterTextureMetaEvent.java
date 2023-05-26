package xfacthd.contex.api.type;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterTextureMetaEvent extends Event implements IModBusEvent
{
    private final Registrar<TextureType> typeRegistrar;
    private final Registrar<ConnectionPredicate> predicateRegistrar;

    @ApiStatus.Internal
    public RegisterTextureMetaEvent(Registrar<TextureType> typeRegistrar, Registrar<ConnectionPredicate> predicateRegistrar)
    {
        this.typeRegistrar = typeRegistrar;
        this.predicateRegistrar = predicateRegistrar;
    }

    public void registerType(ResourceLocation name, TextureType type)
    {
        typeRegistrar.accept(name, type);
    }

    public void registerPredicate(ResourceLocation name, ConnectionPredicate predicate)
    {
        predicateRegistrar.accept(name, predicate);
    }



    public interface Registrar<T> extends BiConsumer<ResourceLocation, T>
    {
        @Override
        void accept(ResourceLocation name, T t);
    }
}
