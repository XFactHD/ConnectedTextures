package io.github.xfacthd.contex.client.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class ExtendedQuadCollectionBuilder extends QuadCollection.Builder implements Consumer<BakedQuad>
{
    @Nullable
    private Direction currCullFace = null;

    @Override
    public void accept(BakedQuad quad)
    {
        if (currCullFace != null)
        {
            addCulledFace(currCullFace, quad);
        }
        else
        {
            addUnculledFace(quad);
        }
    }

    public void setCullFace(@Nullable Direction currCullFace)
    {
        this.currCullFace = currCullFace;
    }
}
