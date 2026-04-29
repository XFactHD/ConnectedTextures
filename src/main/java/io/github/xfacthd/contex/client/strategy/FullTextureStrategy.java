package io.github.xfacthd.contex.client.strategy;

import io.github.xfacthd.contex.api.model.QuadRebaker;
import io.github.xfacthd.contex.api.model.SpriteLookup;
import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.api.utils.Utils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class FullTextureStrategy implements TextureStrategy {
    private static final Map<SpriteType, SpriteTypeTuple> PART_TYPES_BY_TYPE = new IdentityHashMap<>();
    private static final @Nullable SpriteType[] TYPE_MAPPING = makeTypeMapping();
    public static final Set<SpriteType> TYPES = Set.copyOf(Arrays.stream(TYPE_MAPPING).filter(Objects::nonNull).toList());
    public static final FullTextureStrategy INSTANCE = new FullTextureStrategy();

    @Override
    public void makeConnectionQuads(TextureType type, BakedQuad srcQuad, Direction side, byte state, SpriteLookup sprites, Consumer<BakedQuad> output) {
        if (state == 0) {
            output.accept(srcQuad);
            return;
        }

        SpriteType topLeft = computeCornerType(type, state, ConnectionDirection.LEFT, ConnectionDirection.UP, side);
        SpriteType topRight = computeCornerType(type, state, ConnectionDirection.RIGHT, ConnectionDirection.UP, side);
        SpriteType bottomLeft = computeCornerType(type, state, ConnectionDirection.LEFT, ConnectionDirection.DOWN, side);
        SpriteType bottomRight = computeCornerType(type, state, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, side);
        SpriteType fullType = Objects.requireNonNullElse(tryGetType(topLeft, topRight, bottomLeft, bottomRight), SpriteType.NONE);
        QuadRebaker.processRemapOnly(srcQuad, sprites.get(fullType), output);
    }

    private static SpriteType computeCornerType(TextureType type, byte state, ConnectionDirection uDir, ConnectionDirection vDir, Direction side) {
        if (Utils.isY(side)) {
            vDir = vDir.getOpposite();
        } else {
            uDir = uDir.getOpposite();
        }

        boolean xCon = uDir.isSet(state);
        boolean yCon = vDir.isSet(state);
        boolean diagCon = (xCon || yCon) && ConnectionDirection.diagonal(uDir, vDir).isSet(state);
        return type.getConnectedSprite(xCon, yCon, diagCon, side);
    }

    @Override
    public void makeNonCtQuads(BakedQuad srcQuad, Consumer<BakedQuad> output) {
        output.accept(srcQuad);
    }

    @Override
    public Set<SpriteType> computePermittedTypes(Set<SpriteType> spriteTypes) {
        spriteTypes = new HashSet<>(spriteTypes);
        spriteTypes.add(SpriteType.NONE);
        Set<SpriteType> fullTypes = new HashSet<>();
        for (SpriteType topLeft : spriteTypes) {
            for (SpriteType topRight : spriteTypes) {
                for (SpriteType bottomLeft : spriteTypes) {
                    for (SpriteType bottomRight : spriteTypes) {
                        SpriteType type = tryGetType(topLeft, topRight, bottomLeft, bottomRight);
                        if (type != null) {
                            fullTypes.add(type);
                        }
                    }
                }
            }
        }
        return fullTypes;
    }

    @Nullable
    private static SpriteType tryGetType(SpriteType topLeft, SpriteType topRight, SpriteType bottomLeft, SpriteType bottomRight) {
        return TYPE_MAPPING[makeIndex(topLeft, topRight, bottomLeft, bottomRight)];
    }

    public static SpriteTypeTuple getPartTypes(SpriteType type) {
        return Objects.requireNonNull(PART_TYPES_BY_TYPE.get(type));
    }

    private static int makeIndex(SpriteType topLeft, SpriteType topRight, SpriteType bottomLeft, SpriteType bottomRight) {
        return topLeft.getBaseIndex() | (topRight.getBaseIndex() << 3) | (bottomLeft.getBaseIndex() << 6) | (bottomRight.getBaseIndex() << 9);
    }

    private static SpriteType[] makeTypeMapping() {
        SpriteType[] fullTypes = new SpriteType[4096];
        fullTypes[0] = SpriteType.NONE;
        for (SpriteType topLeft : SpriteType.BASE_TYPES) {
            for (SpriteType topRight : SpriteType.BASE_TYPES) {
                for (SpriteType bottomLeft : SpriteType.BASE_TYPES) {
                    for (SpriteType bottomRight : SpriteType.BASE_TYPES) {
                        if (topLeft.connectsVertical() != topRight.connectsVertical()) {
                            continue;
                        }
                        if (bottomLeft.connectsVertical() != bottomRight.connectsVertical()) {
                            continue;
                        }
                        if (topLeft.connectsHorizontal() != bottomLeft.connectsHorizontal()) {
                            continue;
                        }
                        if (topRight.connectsHorizontal() != bottomRight.connectsHorizontal()) {
                            continue;
                        }

                        int index = makeIndex(topLeft, topRight, bottomLeft, bottomRight);
                        if (index == 0) {
                            continue;
                        }

                        SpriteType fullType = new SpriteType("full", topLeft.suffix() + "_" + topRight.suffix() + "_" + bottomLeft.suffix() + "_" + bottomRight.suffix());
                        fullTypes[index] = fullType;
                        PART_TYPES_BY_TYPE.put(fullType, new SpriteTypeTuple(topLeft, topRight, bottomLeft, bottomRight));
                    }
                }
            }
        }
        return fullTypes;
    }

    public record SpriteTypeTuple(SpriteType topLeft, SpriteType topRight, SpriteType bottomLeft, SpriteType bottomRight) { }
}
