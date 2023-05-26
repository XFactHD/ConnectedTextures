package xfacthd.contex.api.type;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.contex.api.state.ConnectionDirection;

public interface ConnectionPredicate
{
    /**
     * Test whether the block at the given position matches the neighboring block at the given neighbor position.
     * @param level The level the block is in
     * @param pos The position the block is at
     * @param otherPos The position of the neighboring block
     * @param state The state of the block
     * @param conDir The {@link ConnectionDirection}
     * @param side The side of the block being checked
     */
    boolean test(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockPos otherPos,
            BlockState state,
            ConnectionDirection conDir,
            Direction side
    );
}
