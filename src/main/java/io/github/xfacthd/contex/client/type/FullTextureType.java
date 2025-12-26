package io.github.xfacthd.contex.client.type;

import io.github.xfacthd.contex.api.type.SpriteType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.DefaultTextureType;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;

public sealed class FullTextureType extends DefaultTextureType permits SimpleTextureType, FullCarpetTextureType
{
    protected static final ConnectionDirection[] CARDINAL_DIRECTIONS = Arrays.stream(ConnectionDirection.values())
            .filter(dir -> !dir.isDiagonal())
            .toArray(ConnectionDirection[]::new);
    private static final ConnectionDirection[] DIAGONAL_DIRECTIONS = Arrays.stream(ConnectionDirection.values())
            .filter(ConnectionDirection::isDiagonal)
            .toArray(ConnectionDirection[]::new);
    private static final EnumSet<SpriteType> SPRITE_TYPES = EnumSet.of(
            SpriteType.HORIZONTAL,
            SpriteType.VERTICAL,
            SpriteType.CROSS,
            SpriteType.FULL
    );
    public static final FullTextureType INSTANCE = new FullTextureType();

    protected FullTextureType() { }

    @Override
    public byte getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    )
    {
        byte connections = 0;
        for (ConnectionDirection dir : CARDINAL_DIRECTIONS)
        {
            connections = testDirection(dir, connections, level, pos, state, side, predicate, occlusionMode);
        }
        for (ConnectionDirection dir : DIAGONAL_DIRECTIONS)
        {
            if (dir.areCardinalNeighborsSet(connections))
            {
                connections = testDirection(dir, connections, level, pos, state, side, predicate, occlusionMode);
            }
        }
        return connections;
    }

    @Override
    @Nullable
    public SpriteType getConnectedSprite(boolean xCon, boolean yCon, boolean diagCon, Direction side)
    {
        if (xCon && yCon)
        {
            return diagCon ? SpriteType.FULL : SpriteType.CROSS;
        }
        else if (xCon)
        {
            return SpriteType.HORIZONTAL;
        }
        else if (yCon)
        {
            return SpriteType.VERTICAL;
        }
        return null;
    }

    @Override
    public EnumSet<SpriteType> getSpriteTypes()
    {
        return SPRITE_TYPES;
    }
}
