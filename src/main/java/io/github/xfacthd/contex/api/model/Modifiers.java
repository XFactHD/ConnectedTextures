package io.github.xfacthd.contex.api.model;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import io.github.xfacthd.contex.api.utils.Utils;

public final class Modifiers
{
    private static final QuadModifier.Modifier NOOP_MODIFIER = data -> true;

    /**
     * Cuts the quad pointing upwards or downwards at the edge given by the given {@code cutDir}
     * @param cutDir The direction towards the cut edge
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cutTopBottom(Direction cutDir, float length)
    {
        return Mth.equal(length, 1F) ? NOOP_MODIFIER : data ->
        {
            Direction quadDir = data.quad().direction();
            Preconditions.checkState(Utils.isY(quadDir), "Quad direction must be vertical");
            Preconditions.checkState(quadDir.getAxis() != cutDir.getAxis(), "Cut direction must be prependicular to the quad direction");

            boolean xAxis = Utils.isX(cutDir);
            boolean positive = Utils.isPositive(cutDir);
            boolean up = quadDir == Direction.UP;

            int idxR = xAxis ? (positive ? 2 : 1) : ((up == positive) ? 1 : 0);
            int idxL = xAxis ? (positive ? 3 : 0) : ((up == positive) ? 2 : 3);

            float target = positive ? length : 1F - length;

            int vertIdxR = xAxis ? (positive ? 1 : 3) : (up ? (positive ? 0 : 2) : (positive ? 1 : 3));
            int vertIdxL = xAxis ? (positive ? 0 : 2) : (up ? (positive ? 3 : 1) : (positive ? 2 : 0));
            int coordIdx = xAxis ? 0 : 2;

            if (positive && (Utils.isHigher(data.pos(vertIdxR, coordIdx), target) || Utils.isHigher(data.pos(vertIdxL, coordIdx), target)))
            {
                return false;
            }
            if (!positive && (Utils.isLower(data.pos(vertIdxR, coordIdx), target) || Utils.isLower(data.pos(vertIdxL, coordIdx), target)))
            {
                return false;
            }

            float xz1 = data.pos(idxR, coordIdx);
            float xz2 = data.pos(idxL, coordIdx);

            float toXZ1 = positive ? Math.min(xz1, target) : Math.max(xz1, target);
            float toXZ2 = positive ? Math.min(xz2, target) : Math.max(xz2, target);

            if (Mth.equal(xz1, toXZ1) && Mth.equal(xz2, toXZ2))
            {
                return true;
            }

            boolean rotated = data.uvRotated;

            if (xAxis)
            {
                ModelUtils.remapUV(data, data.pos(1, coordIdx), data.pos(2, coordIdx), toXZ1, 1, 2, idxR, false, rotated);
                ModelUtils.remapUV(data, data.pos(0, coordIdx), data.pos(3, coordIdx), toXZ2, 0, 3, idxL, false, rotated);
            }
            else
            {
                ModelUtils.remapUV(data, data.pos(1, coordIdx), data.pos(0, coordIdx), toXZ1, 0, 1, idxR, true, rotated);
                ModelUtils.remapUV(data, data.pos(2, coordIdx), data.pos(3, coordIdx), toXZ2, 3, 2, idxL, true, rotated);
            }

            data.pos(idxR, coordIdx, toXZ1);
            data.pos(idxL, coordIdx, toXZ2);

            return true;
        };
    }

    /**
     * Cuts the quad pointing horizontally at the top or bottom edge given by {@code downwards}
     * @param downwards Whether the starting edge should be top (true) or bottom (false)
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cutSideUpDown(boolean downwards, float length)
    {
        return Mth.equal(length, 1F) ? NOOP_MODIFIER : data ->
        {
            Direction quadDir = data.quad().direction();
            Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

            float target = downwards ? 1F - length : length;

            if (downwards && (Utils.isLower(data.pos(0, 1), target) || Utils.isLower(data.pos(3, 1), target)))
            {
                return false;
            }
            if (!downwards && (Utils.isHigher(data.pos(1, 1), target) || Utils.isHigher(data.pos(2, 1), target)))
            {
                return false;
            }

            int idx1 = downwards ? 1 : 0;
            int idx2 = downwards ? 2 : 3;

            float y1 = data.pos(idx1, 1);
            float y2 = data.pos(idx2, 1);

            float toY1 = downwards ? Math.max(y1, target) : Math.min(y1, target);
            float toY2 = downwards ? Math.max(y2, target) : Math.min(y2, target);

            //noinspection SuspiciousNameCombination
            if (Mth.equal(y1, toY1) && Mth.equal(y2, toY2))
            {
                return true;
            }

            boolean rotated = data.uvRotated;
            ModelUtils.remapUV(data, data.pos(1, 1), data.pos(0, 1), toY1, 0, 1, idx1, true, rotated);
            ModelUtils.remapUV(data, data.pos(2, 1), data.pos(3, 1), toY2, 3, 2, idx2, true, rotated);

            data.pos(idx1, 1, toY1);
            data.pos(idx2, 1, toY2);

            return true;
        };
    }

    /**
     * Cuts the quad pointing horizontally at the edge given by {@code cutDir}
     * @param towardsRight The direction towards the cut edge
     * @param length The target length from the starting edge
     */
    public static QuadModifier.Modifier cutSideLeftRight(boolean towardsRight, float length)
    {
        return Mth.equal(length, 1F) ? NOOP_MODIFIER : data ->
        {
            Direction quadDir = data.quad().direction();
            Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

            boolean positive = Utils.isPositive(towardsRight ? quadDir.getCounterClockWise() : quadDir.getClockWise());
            int coordIdx = Utils.isX(quadDir) ? 2 : 0;
            int vertIdxTop = towardsRight ? 3 : 0;
            int vertIdxBot = towardsRight ? 2 : 1;

            float target = positive ? 1F - length : length;

            if (positive && (Utils.isLower(data.pos(vertIdxTop, coordIdx), target) || Utils.isLower(data.pos(vertIdxBot, coordIdx), target)))
            {
                return false;
            }
            if (!positive && (Utils.isHigher(data.pos(vertIdxTop, coordIdx), target) || Utils.isHigher(data.pos(vertIdxBot, coordIdx), target)))
            {
                return false;
            }

            int idx1 = towardsRight ? 0 : 3;
            int idx2 = towardsRight ? 1 : 2;

            float xz1 = data.pos(idx1, coordIdx);
            float xz2 = data.pos(idx2, coordIdx);

            float toXZ1 = positive ? Math.max(xz1, target) : Math.min(xz1, target);
            float toXZ2 = positive ? Math.max(xz2, target) : Math.min(xz2, target);

            if (Mth.equal(xz1, toXZ1) && Mth.equal(xz2, toXZ2))
            {
                return true;
            }

            boolean rotated = data.uvRotated;
            ModelUtils.remapUV(data, data.pos(0, coordIdx), data.pos(3, coordIdx), toXZ1, 0, 3, idx1, false, rotated);
            ModelUtils.remapUV(data, data.pos(1, coordIdx), data.pos(2, coordIdx), toXZ2, 1, 2, idx2, false, rotated);

            data.pos(idx1, coordIdx, toXZ1);
            data.pos(idx2, coordIdx, toXZ2);

            return true;
        };
    }

    /**
     * Map a different texture onto this quad. The target sprite must cover the block face in the
     * same manner as the original sprite.
     *
     * @param targetSprite The texture to apply to the quad, must be stitched to the block atlas
     */
    public static QuadModifier.Modifier remapTexture(TextureAtlasSprite targetSprite)
    {
        return data ->
        {
            TextureAtlasSprite srcSprite = data.sprite;

            for (int i = 0; i < 4; i++)
            {
                float uRel = getRelUV(data, srcSprite, i, 0);
                float vRel = getRelUV(data, srcSprite, i, 1);
                data.uv(i, targetSprite.getU(uRel), targetSprite.getV(vRel));
            }

            data.sprite(targetSprite);

            return true;
        };
    }

    private static float getRelUV(QuadData data, TextureAtlasSprite sprite, int vertex, int uvIdx)
    {
        float uv0 = uvIdx == 0 ? sprite.getU0() : sprite.getV0();
        float uv1 = uvIdx == 0 ? sprite.getU1() : sprite.getV1();
        return (data.uv(vertex, uvIdx) - uv0) / (uv1 - uv0);
    }

    private Modifiers() {}
}
