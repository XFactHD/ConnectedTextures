package xfacthd.contex.api.model;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import xfacthd.contex.api.utils.Utils;

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
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkState(Utils.isY(quadDir), "Quad direction must be vertical");
            Preconditions.checkState(quadDir.getAxis() != cutDir.getAxis(), "Cut direction must be prependicular to the quad direction");

            boolean xAxis = Utils.isX(cutDir);
            boolean positive = Utils.isPositive(cutDir);
            boolean up = quadDir == Direction.UP;

            int idxR = xAxis ? (positive ? 2 : 1) : ((up == positive) ? 1 : 0);
            int idxL = xAxis ? (positive ? 3 : 0) : ((up == positive) ? 2 : 3);

            float[][] pos = data.pos();
            float target = positive ? length : 1F - length;

            int vertIdxR = xAxis ? (positive ? 1 : 3) : (up ? (positive ? 0 : 2) : (positive ? 1 : 3));
            int vertIdxL = xAxis ? (positive ? 0 : 2) : (up ? (positive ? 3 : 1) : (positive ? 2 : 0));
            int coordIdx = xAxis ? 0 : 2;

            if (positive && (Utils.isHigher(pos[vertIdxR][coordIdx], target) || Utils.isHigher(pos[vertIdxL][coordIdx], target)))
            {
                return false;
            }
            if (!positive && (Utils.isLower(pos[vertIdxR][coordIdx], target) || Utils.isLower(pos[vertIdxL][coordIdx], target)))
            {
                return false;
            }

            float xz1 = pos[idxR][coordIdx];
            float xz2 = pos[idxL][coordIdx];

            pos[idxR][coordIdx] = positive ? Math.min(xz1, target) : Math.max(xz1, target);
            pos[idxL][coordIdx] = positive ? Math.min(xz2, target) : Math.max(xz2, target);

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
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

            float[][] pos = data.pos();
            float target = downwards ? 1F - length : length;

            if (downwards && (Utils.isLower(pos[0][1], target) || Utils.isLower(pos[3][1], target)))
            {
                return false;
            }
            if (!downwards && (Utils.isHigher(pos[1][1], target) || Utils.isHigher(pos[2][1], target)))
            {
                return false;
            }

            int idx1 = downwards ? 1 : 0;
            int idx2 = downwards ? 2 : 3;

            float y1 = pos[idx1][1];
            float y2 = pos[idx2][1];

            pos[idx1][1] = downwards ? Math.max(y1, target) : Math.min(y1, target);
            pos[idx2][1] = downwards ? Math.max(y2, target) : Math.min(y2, target);

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
            Direction quadDir = data.quad().getDirection();
            Preconditions.checkState(!Utils.isY(quadDir), "Quad direction must be horizontal");

            boolean positive = Utils.isPositive(towardsRight ? quadDir.getCounterClockWise() : quadDir.getClockWise());
            int coordIdx = Utils.isX(quadDir) ? 2 : 0;
            int vertIdxTop = towardsRight ? 3 : 0;
            int vertIdxBot = towardsRight ? 2 : 1;

            float[][] pos = data.pos();
            float target = positive ? 1F - length : length;

            if (positive && (Utils.isLower(pos[vertIdxTop][coordIdx], target) || Utils.isLower(pos[vertIdxBot][coordIdx], target)))
            {
                return false;
            }
            if (!positive && (Utils.isHigher(pos[vertIdxTop][coordIdx], target) || Utils.isHigher(pos[vertIdxBot][coordIdx], target)))
            {
                return false;
            }

            int idx1 = towardsRight ? 0 : 3;
            int idx2 = towardsRight ? 1 : 2;

            float xz1 = pos[idx1][coordIdx];
            float xz2 = pos[idx2][coordIdx];

            pos[idx1][coordIdx] = positive ? Math.max(xz1, target) : Math.min(xz1, target);
            pos[idx2][coordIdx] = positive ? Math.max(xz2, target) : Math.min(xz2, target);

            return true;
        };
    }

    /**
     * Map a different texture onto this quad with the given min and max UV coordinates being in relation to a full block face
     * @param targetSprite The texture to apply to the quad, must be stitched to the block atlas
     * @param minU The min U coordinate in the range 0-1
     * @param minV The min V coordinate in the range 0-1
     * @param maxU The max U coordinate in the range 0-1
     * @param maxV The max V coordinate in the range 0-1
     */
    public static QuadModifier.Modifier remapTexture(TextureAtlasSprite targetSprite, float minU, float minV, float maxU, float maxV)
    {
        return data ->
        {
            float shrinkRatio = targetSprite.uvShrinkRatio();
            UVInfo uvInfo = ModelUtils.getUVInfo(data.quad().getDirection());

            float uSize = maxU - minU;
            float vSize = maxV - minV;
            float uCenter = (minU + minU + maxU + maxU) / 4F;
            float vCenter = (minV + minV + maxV + maxV) / 4F;

            float[] uRel = new float[4];
            float[] vRel = new float[4];
            for (int i = 0; i < 4; i++)
            {
                float[] pos = data.pos()[i];
                float uAbs = uvInfo.uInv() ? (1F - pos[uvInfo.uIdx()]) : pos[uvInfo.uIdx()];
                float vAbs = uvInfo.vInv() ? (1F - pos[uvInfo.vIdx()]) : pos[uvInfo.vIdx()];
                uRel[i] = Mth.lerp(shrinkRatio, (uAbs * uSize) + minU, uCenter);
                vRel[i] = Mth.lerp(shrinkRatio, (vAbs * vSize) + minV, vCenter);
            }

            for (int i = 0; i < 4; i++)
            {
                float[] uv = data.uv()[i];
                uv[0] = targetSprite.getU(uRel[i]);
                uv[1] = targetSprite.getV(vRel[i]);
            }

            data.sprite().setValue(targetSprite);

            return true;
        };
    }



    private Modifiers() {}
}
