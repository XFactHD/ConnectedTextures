package xfacthd.contex.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;

import java.util.Arrays;
import java.util.Objects;

record QuadCacheKey(Direction side, RenderType renderType, byte[] ctStates)
{
    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        QuadCacheKey that = (QuadCacheKey) o;
        return side == that.side && Objects.equals(renderType, that.renderType) && Arrays.equals(ctStates, that.ctStates);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(side, renderType, Arrays.hashCode(ctStates));
    }
}
