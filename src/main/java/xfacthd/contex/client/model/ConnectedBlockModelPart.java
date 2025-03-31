package xfacthd.contex.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

record ConnectedBlockModelPart(
        QuadCollection quads,
        boolean useAmbientOcclusion,
        TextureAtlasSprite particleIcon,
        RenderType renderType,
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
    public RenderType getRenderType(BlockState state)
    {
        return renderType;
    }

    public static ConnectedBlockModelPart of(BlockModelPart srcPart, BlockState state, QuadCollection quads, int metaIdx, int texIdx)
    {
        //noinspection deprecation
        boolean ao = srcPart.useAmbientOcclusion();
        TextureAtlasSprite particleIcon = srcPart.particleIcon();
        RenderType renderType = srcPart.getRenderType(state);
        return new ConnectedBlockModelPart(quads, ao, particleIcon, renderType, metaIdx, texIdx);
    }
}
