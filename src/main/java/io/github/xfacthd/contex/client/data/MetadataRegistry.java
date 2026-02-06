package io.github.xfacthd.contex.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.RegisterTextureMetaEvent;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.fml.ModLoader;

import java.util.function.Function;

public final class MetadataRegistry
{
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, TextureType> TYPES = new ExtraCodecs.LateBoundIdMapper<>();
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ConnectionPredicate>> PREDICATES = new ExtraCodecs.LateBoundIdMapper<>();
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, TextureStrategy> STRATEGIES = new ExtraCodecs.LateBoundIdMapper<>();
    public static final Codec<TextureType> TYPE_CODEC = TYPES.codec(Identifier.CODEC);
    public static final Codec<ConnectionPredicate> PREDICATE_CODEC = PREDICATES.codec(Identifier.CODEC).dispatch(ConnectionPredicate::codec, Function.identity());
    public static final Codec<TextureStrategy> STRATEGY_CODEC = STRATEGIES.codec(Identifier.CODEC);

    public static void init()
    {
        ModLoader.postEvent(new RegisterTextureMetaEvent(TYPES::put, PREDICATES::put, STRATEGIES::put));
    }

    private MetadataRegistry() { }
}
