package io.github.xfacthd.contex.client.type;

import io.github.xfacthd.contex.api.type.SpriteType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import io.github.xfacthd.contex.api.utils.Constants;

import java.util.EnumSet;

public sealed class SimpleTextureType extends FullTextureType permits SimpleCarpetTextureType
{
    private static final EnumSet<SpriteType> SPRITE_TYPES = EnumSet.of(
            SpriteType.HORIZONTAL,
            SpriteType.VERTICAL,
            SpriteType.FULL
    );
    public static final SimpleTextureType INSTANCE = new SimpleTextureType();

    protected SimpleTextureType() { }

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
        return connections;
    }

    @Override
    public void postProcessConnections(byte[] stateMap)
    {
        for (Direction side : Constants.DIRECTIONS)
        {
            byte connections = stateMap[side.ordinal()];
            if (ConnectionDirection.UP.isSet(connections) && ConnectionDirection.LEFT.isSet(connections))
            {
                connections = ConnectionDirection.UP_LEFT.set(connections);
            }
            if (ConnectionDirection.DOWN.isSet(connections) && ConnectionDirection.LEFT.isSet(connections))
            {
                connections = ConnectionDirection.DOWN_LEFT.set(connections);
            }
            if (ConnectionDirection.UP.isSet(connections) && ConnectionDirection.RIGHT.isSet(connections))
            {
                connections = ConnectionDirection.UP_RIGHT.set(connections);
            }
            if (ConnectionDirection.DOWN.isSet(connections) && ConnectionDirection.RIGHT.isSet(connections))
            {
                connections = ConnectionDirection.DOWN_RIGHT.set(connections);
            }
            stateMap[side.ordinal()] = connections;
        }
    }

    @Override
    public EnumSet<SpriteType> getSpriteTypes()
    {
        return SPRITE_TYPES;
    }
}
