package io.github.xfacthd.contex.api.type;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.util.StringRepresentable;

import java.util.Set;

public final class SpriteType implements StringRepresentable {
    private static final Set<SpriteType> TYPES = new ReferenceOpenHashSet<>();
    public static final Codec<SpriteType> CODEC = Codec.lazyInitialized(() -> StringRepresentable.fromValues(() -> TYPES.toArray(SpriteType[]::new)));

    public static final SpriteType NONE = new SpriteType("none", "none", false, false, 0);
    public static final SpriteType HORIZONTAL = new SpriteType("compact", "hor", true, false, 1);
    public static final SpriteType VERTICAL = new SpriteType("compact", "vert", false, true, 2);
    public static final SpriteType CROSS = new SpriteType("compact", "cross", true, true, 3);
    public static final SpriteType FULL = new SpriteType("compact", "full", true, true, 4);

    public static final Set<SpriteType> BASE_TYPES = Set.of(NONE, HORIZONTAL, VERTICAL, CROSS, FULL);

    private final String name;
    private final String suffix;
    private final boolean connectsHorizontal;
    private final boolean connectsVertical;
    private final boolean baseType;
    private final int baseIndex;

    public SpriteType(String group, String suffix) {
        this(group, suffix, false, false, -1);
    }

    private SpriteType(String group, String suffix, boolean connectsHorizontal, boolean connectsVertical, int baseIndex) {
        this.name = group + "/" + suffix;
        this.suffix = suffix;
        this.connectsHorizontal = connectsHorizontal;
        this.connectsVertical = connectsVertical;
        this.baseType = baseIndex != -1;
        this.baseIndex = baseIndex;
        TYPES.add(this);
    }

    public String suffix() {
        return suffix;
    }

    public boolean connectsHorizontal() {
        Preconditions.checkState(baseType);
        return connectsHorizontal;
    }

    public boolean connectsVertical() {
        Preconditions.checkState(baseType);
        return connectsVertical;
    }

    public int getBaseIndex() {
        Preconditions.checkState(baseType);
        return baseIndex;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
