package io.github.xfacthd.contex.client.type;

import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.utils.Utils;
import net.minecraft.core.Direction;

import java.util.Set;

public final class RotatingPillarTextureType extends PillarTextureType
{
    public static final RotatingPillarTextureType X = new RotatingPillarTextureType(Direction.Axis.X);
    public static final RotatingPillarTextureType Y = new RotatingPillarTextureType(Direction.Axis.Y);
    public static final RotatingPillarTextureType Z = new RotatingPillarTextureType(Direction.Axis.Z);
    private static final Set<SpriteType> SPRITE_TYPES = Set.of(SpriteType.HORIZONTAL, SpriteType.VERTICAL);

    private final SpriteType spriteHor;
    private final SpriteType spriteVert;

    private RotatingPillarTextureType(Direction.Axis axis)
    {
        super(axis);
        switch (axis)
        {
            case X -> spriteHor = spriteVert = SpriteType.HORIZONTAL;
            case Y -> spriteHor = spriteVert = SpriteType.VERTICAL;
            case Z ->
            {
                spriteHor = SpriteType.HORIZONTAL;
                spriteVert = SpriteType.VERTICAL;
            }
            default -> throw new AssertionError();
        }
    }

    @Override
    public SpriteType getConnectedSprite(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if (xCon || yCon)
        {
            return Utils.isY(side) ? spriteVert : spriteHor;
        }
        return SpriteType.NONE;
    }

    @Override
    public Set<SpriteType> getSpriteTypes()
    {
        return SPRITE_TYPES;
    }
}
