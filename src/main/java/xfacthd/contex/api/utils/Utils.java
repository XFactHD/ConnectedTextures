package xfacthd.contex.api.utils;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class Utils
{
    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    /**
     * Check if the left hand value is lower than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is lower than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isLower(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs < rhs;
    }

    /**
     * Check if the left hand value is higher than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is higher than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isHigher(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs > rhs;
    }

    public static boolean isPositive(Direction dir)
    {
        return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    }

    public static boolean isX(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.X;
    }

    public static boolean isY(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.Y;
    }

    public static boolean isZ(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.Z;
    }

    public static void addQuads(QuadCollection.Builder builder, @Nullable Direction side, List<BakedQuad> quads)
    {
        if (side == null)
        {
            for (BakedQuad quad : quads)
            {
                builder.addUnculledFace(quad);
            }
        }
        else
        {
            for (BakedQuad quad : quads)
            {
                builder.addCulledFace(side, quad);
            }
        }
    }



    private Utils() { }
}
