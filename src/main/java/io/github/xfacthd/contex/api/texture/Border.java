package io.github.xfacthd.contex.api.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.util.ExtraCodecs;

public record Border(int left, int top, int right, int bottom, boolean mirrorParallel, boolean mirrorPerpendicular, boolean copyFromOppositeEdge, boolean synthesizeInnerCorners)
{
    public static final Codec<Border> CODEC = RecordCodecBuilder.<Border>create(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("left").forGetter(Border::left),
            ExtraCodecs.POSITIVE_INT.fieldOf("top").forGetter(Border::top),
            ExtraCodecs.POSITIVE_INT.fieldOf("right").forGetter(Border::right),
            ExtraCodecs.POSITIVE_INT.fieldOf("bottom").forGetter(Border::bottom),
            Codec.BOOL.optionalFieldOf("mirror_parallel", false).forGetter(Border::mirrorParallel),
            Codec.BOOL.optionalFieldOf("mirror_perpendicular", false).forGetter(Border::mirrorPerpendicular),
            Codec.BOOL.optionalFieldOf("copy_from_opposite_edge", false).forGetter(Border::copyFromOppositeEdge),
            Codec.BOOL.optionalFieldOf("synthesize_inner_corners", false).forGetter(Border::synthesizeInnerCorners)
    ).apply(inst, Border::new)).validate(Border::validate);

    public Border(int size)
    {
        this(size, false, false, false, false);
    }

    public Border(int size, boolean mirrorParallel, boolean mirrorPerpendicular, boolean copyFromOppositeEdge, boolean synthesizeInnerCorners)
    {
        this(size, size, size, size, mirrorParallel, mirrorPerpendicular, copyFromOppositeEdge, synthesizeInnerCorners);
    }

    public Border(int left, int top, int right, int bottom)
    {
        this(left, top, right, bottom, false, false, false, false);
    }

    public boolean canApplyTo(FrameSize size)
    {
        return left + right <= size.width() && top + bottom <= size.height();
    }

    public boolean canSynthesizeCorners()
    {
        return left == top && left == bottom && left == right && !mirrorParallel && !mirrorPerpendicular;
    }

    private static DataResult<Border> validate(Border border)
    {
        if (border.synthesizeInnerCorners && !border.canSynthesizeCorners())
        {
            return DataResult.error(() -> "Cannot synthesize inner corners with varying edge sizes or mirroring");
        }
        return DataResult.success(border);
    }
}
