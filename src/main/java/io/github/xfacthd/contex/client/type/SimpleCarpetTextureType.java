package io.github.xfacthd.contex.client.type;

import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import io.github.xfacthd.contex.api.utils.Utils;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public final class SimpleCarpetTextureType extends SimpleTextureType {
    public static final SimpleCarpetTextureType[] TYPES = Utils.fillArray(
            new SimpleCarpetTextureType[6], idx -> new SimpleCarpetTextureType(Direction.from3DDataValue(idx))
    );

    private final Direction.Axis axis;
    private final Direction dir;
    private final EnumSet<Direction> affectedFaces;

    private SimpleCarpetTextureType(Direction dir) {
        this.axis = dir.getAxis();
        this.dir = dir;
        this.affectedFaces = EnumSet.of(dir, dir.getOpposite());
    }

    @Override
    public byte getConnectionState(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            Direction side,
            ConnectionPredicate predicate,
            OcclusionMode occlusionMode
    ) {
        if (side.getAxis() == axis) {
            OcclusionMode realOcclusionMode = side == dir ? occlusionMode.except(OcclusionMode.SELF) : OcclusionMode.NONE;
            return super.getConnectionState(level, pos, state, side, predicate, realOcclusionMode);
        }
        return 0;
    }

    @Override
    public EnumSet<Direction> getAffectedFaces() {
        return affectedFaces;
    }
}
