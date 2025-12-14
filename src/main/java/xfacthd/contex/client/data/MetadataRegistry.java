package xfacthd.contex.client.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.fml.ModLoader;
import xfacthd.contex.api.type.ConnectionPredicate;
import xfacthd.contex.api.type.RegisterTextureMetaEvent;
import xfacthd.contex.api.type.TextureType;

import java.util.function.Function;

public final class MetadataRegistry
{
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, TextureType> TYPES = new ExtraCodecs.LateBoundIdMapper<>();
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ConnectionPredicate>> PREDICATES = new ExtraCodecs.LateBoundIdMapper<>();
    public static final Codec<TextureType> TYPE_CODEC = TYPES.codec(Identifier.CODEC);
    public static final Codec<ConnectionPredicate> PREDICATE_CODEC = PREDICATES.codec(Identifier.CODEC).dispatch(ConnectionPredicate::codec, Function.identity());

    public static void init()
    {
        ModLoader.postEvent(new RegisterTextureMetaEvent(TYPES::put, PREDICATES::put));
    }

    private MetadataRegistry() { }
}
