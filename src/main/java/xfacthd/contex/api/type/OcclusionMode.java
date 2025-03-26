package xfacthd.contex.api.type;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;

public enum OcclusionMode implements StringRepresentable
{
    /**
     * Connections will never be occluded
     */
    NONE(false, false),
    /**
     * Connections will be occluded by blocks that match the given {@link ConnectionPredicate}
     */
    SELF(true, false),
    /**
     * Connections will be occluded by blocks which occlude this block according to
     * {@link Block#shouldRenderFace(BlockGetter, BlockPos, BlockState, BlockState, Direction)}
     */
    SOLID(false, true),
    /**
     * Connections will be occluded by blocks matching {@link #SELF} and/or {@link #SOLID}
     */
    SOLID_OR_SELF(true, true);

    public static final Codec<OcclusionMode> CODEC = StringRepresentable.fromEnum(OcclusionMode::values);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final boolean occludedBySelf;
    private final boolean occludedBySolid;

    OcclusionMode(boolean occludedBySelf, boolean occludedBySolid)
    {
        this.occludedBySelf = occludedBySelf;
        this.occludedBySolid = occludedBySolid;
    }

    public boolean isOccludedBySelf()
    {
        return occludedBySelf;
    }

    public boolean isOccludedBySolid()
    {
        return occludedBySolid;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}
