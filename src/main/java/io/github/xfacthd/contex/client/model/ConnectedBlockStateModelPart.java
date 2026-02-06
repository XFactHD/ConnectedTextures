package io.github.xfacthd.contex.client.model;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.List;

record ConnectedBlockStateModelPart(
        QuadCollection quads,
        boolean useAmbientOcclusion,
        Material.Baked particleMaterial,
        int metaIdx,
        int texIdx
) implements BlockStateModelPart
{
    @Override
    public List<BakedQuad> getQuads(@Nullable Direction side)
    {
        return quads.getQuads(side);
    }

    @Override
    @BakedQuad.MaterialFlags
    public int materialFlags()
    {
        return quads.materialFlags();
    }

    @SuppressWarnings("deprecation")
    public static ConnectedBlockStateModelPart of(BlockStateModelPart srcPart, QuadCollection quads, int metaIdx, int texIdx)
    {
        return new ConnectedBlockStateModelPart(quads, srcPart.useAmbientOcclusion(), srcPart.particleMaterial(), metaIdx, texIdx);
    }
}
