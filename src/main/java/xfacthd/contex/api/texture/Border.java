package xfacthd.contex.api.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.util.ExtraCodecs;

public record Border(int left, int top, int right, int bottom, boolean mirrorParallel, boolean mirrorPerpendicular)
{
    public static final Codec<Border> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("left").forGetter(Border::left),
            ExtraCodecs.POSITIVE_INT.fieldOf("top").forGetter(Border::top),
            ExtraCodecs.POSITIVE_INT.fieldOf("right").forGetter(Border::right),
            ExtraCodecs.POSITIVE_INT.fieldOf("bottom").forGetter(Border::bottom),
            Codec.BOOL.optionalFieldOf("mirror_parallel", false).forGetter(Border::mirrorParallel),
            Codec.BOOL.optionalFieldOf("mirror_perpendicular", false).forGetter(Border::mirrorPerpendicular)
    ).apply(inst, Border::new));

    public Border(int size)
    {
        this(size, false, false);
    }

    public Border(int size, boolean mirrorParallel, boolean mirrorPerpendicular)
    {
        this(size, size, size, size, mirrorParallel, mirrorPerpendicular);
    }

    public Border(int left, int top, int right, int bottom)
    {
        this(left, top, right, bottom, false, false);
    }

    public boolean canApplyTo(FrameSize size)
    {
        return left + right <= size.width() && top + bottom <= size.height();
    }
}
