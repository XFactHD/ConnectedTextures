package io.github.xfacthd.contex.api.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface ConnectionPredicate
{
    /**
     * Test whether the block at the given position matches the neighboring block at the given neighbor position.
     *
     * @param level     The level the block is in
     * @param pos       The position the block is at
     * @param otherPos  The position of the neighboring block
     * @param state     The state of the block
     * @param side      The side of the block being checked
     * @param otherSide The side of the neighboring block being checked
     */
    boolean test(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockPos otherPos,
            BlockState state,
            Direction side,
            Direction otherSide
    );

    MapCodec<? extends ConnectionPredicate> codec();
}
