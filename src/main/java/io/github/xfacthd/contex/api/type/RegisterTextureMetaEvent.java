package io.github.xfacthd.contex.api.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;

public final class RegisterTextureMetaEvent extends Event implements IModBusEvent
{
    private final Registrar<TextureType> typeRegistrar;
    private final Registrar<MapCodec<? extends ConnectionPredicate>> predicateRegistrar;
    private final Registrar<TextureStrategy> strategyRegistrar;

    @ApiStatus.Internal
    public RegisterTextureMetaEvent(
            Registrar<TextureType> typeRegistrar,
            Registrar<MapCodec<? extends ConnectionPredicate>> predicateRegistrar,
            Registrar<TextureStrategy> strategyRegistrar
    )
    {
        this.typeRegistrar = typeRegistrar;
        this.predicateRegistrar = predicateRegistrar;
        this.strategyRegistrar = strategyRegistrar;
    }

    public void registerType(Identifier name, TextureType type)
    {
        typeRegistrar.accept(name, type);
    }

    public void registerPredicate(Identifier name, MapCodec<? extends ConnectionPredicate> predicate)
    {
        predicateRegistrar.accept(name, predicate);
    }

    public void registerStrategy(Identifier name, TextureStrategy strategy)
    {
        strategyRegistrar.accept(name, strategy);
    }

    public interface Registrar<T> extends BiConsumer<Identifier, T>
    {
        @Override
        void accept(Identifier name, T t);
    }
}
