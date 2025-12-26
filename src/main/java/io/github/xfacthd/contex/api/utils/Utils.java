package io.github.xfacthd.contex.api.utils;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Utils
{
    public static Identifier rl(String path)
    {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
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

    public static <T> T[] fillArray(T[] array, IntFunction<? extends T> generator)
    {
        Arrays.setAll(array, generator);
        return array;
    }

    public static <K, V> Codec<Reference2ObjectMap<K, V>> ref2ObjMapCodec(Codec<K> keyCodec, Codec<V> valueCodec)
    {
        return Codec.unboundedMap(keyCodec, valueCodec).xmap(Reference2ObjectOpenHashMap::new, Function.identity());
    }

    private Utils() { }
}
