package io.github.xfacthd.contex.api.type;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SpriteType implements StringRepresentable
{
    HORIZONTAL("hor"),
    VERTICAL("vert"),
    CROSS("cross"),
    FULL("full"),
    ;

    public static final Codec<SpriteType> CODEC = StringRepresentable.fromEnum(SpriteType::values);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final String suffix;

    SpriteType(String suffix)
    {
        this.suffix = suffix;
    }

    public String suffix()
    {
        return suffix;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
