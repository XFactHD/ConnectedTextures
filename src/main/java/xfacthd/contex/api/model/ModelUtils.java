package xfacthd.contex.api.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class ModelUtils
{
    public static TextureAtlasSprite getSprite(Identifier loc)
    {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(loc);
    }

    /**
     * Maps a coordinate 'coordTo' between the given coordinates 'coord1' and 'coord2'
     * onto the UV range they occupy as given by the values at 'uv1' and 'uv2' in the 'uv'
     * array, calculates the target UV coordinate corresponding to the value of 'coordTo'
     * and places it at 'uvTo' in the 'uv' array
     * @param data The {@link QuadData} being operated on
     * @param coord1 The first coordinate
     * @param coord2 The second coordinate
     * @param coordTo The target coordinate, must lie between coord1 and coord2
     * @param uv1 The first UV texture coordinate
     * @param uv2 The second UV texture coordinate
     * @param uvTo The target UV texture coordinate
     * @param vAxis Whether the modification should happen on the V axis or the U axis
     * @param rotated Whether the UVs are rotated
     */
    public static void remapUV(
            QuadData data,
            float coord1,
            float coord2,
            float coordTo,
            int uv1,
            int uv2,
            int uvTo,
            boolean vAxis,
            boolean rotated
    )
    {
        float coordMin = Math.min(coord1, coord2);
        float coordMax = Math.max(coord1, coord2);

        int uvIdx = rotated != vAxis ? 1 : 0;

        float uvAbs1 = data.uv(uv1, uvIdx);
        float uvAbs2 = data.uv(uv2, uvIdx);
        float uvAbsMin = Math.min(uvAbs1, uvAbs2);
        float uvAbsMax = Math.max(uvAbs1, uvAbs2);
        boolean invert = ((coord2 > coord1) ^ (uvAbs2 > uvAbs1)) != vAxis;

        if (coordTo == coordMin)
        {
            data.uv(uvTo, uvIdx, (invert) ? uvAbsMax : uvAbsMin);
        }
        else if (coordTo == coordMax)
        {
            data.uv(uvTo, uvIdx, (invert) ? uvAbsMin : uvAbsMax);
        }
        else
        {
            float mult = (coordTo - coordMin) / (coordMax - coordMin);
            if (invert) mult = 1F - mult;
            data.uv(uvTo, uvIdx, Mth.lerp(mult, uvAbsMin, uvAbsMax));
        }
    }

    public static boolean isQuadRotated(QuadData data)
    {
        return (Mth.equal(data.uv(0, 1), data.uv(1, 1)) || Mth.equal(data.uv(3, 1), data.uv(2, 1))) &&
               (Mth.equal(data.uv(1, 0), data.uv(2, 0)) || Mth.equal(data.uv(0, 0), data.uv(3, 0)));
    }

    private ModelUtils() { }
}