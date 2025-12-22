package io.github.xfacthd.contex.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;

record ConnectedBlockModelPart(
        QuadCollection quads,
        boolean useAmbientOcclusion,
        TextureAtlasSprite particleIcon,
        ChunkSectionLayer chunkLayer,
        int metaIdx,
        int texIdx
) implements BlockModelPart
{
    @Override
    public List<BakedQuad> getQuads(@Nullable Direction side)
    {
        return quads.getQuads(side);
    }

    @Override
    public ChunkSectionLayer getRenderType(BlockState state)
    {
        return chunkLayer;
    }

    public static ConnectedBlockModelPart of(BlockModelPart srcPart, BlockState state, QuadCollection quads, int metaIdx, int texIdx)
    {
        //noinspection deprecation
        boolean ao = srcPart.useAmbientOcclusion();
        TextureAtlasSprite particleIcon = srcPart.particleIcon();
        ChunkSectionLayer chunkLayer = srcPart.getRenderType(state);
        return new ConnectedBlockModelPart(quads, ao, particleIcon, chunkLayer, metaIdx, texIdx);
    }
}
