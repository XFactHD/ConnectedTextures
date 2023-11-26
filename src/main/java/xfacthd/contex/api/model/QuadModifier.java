package xfacthd.contex.api.model;

import net.minecraft.client.renderer.block.model.BakedQuad;

public final class QuadModifier
{
    private static final QuadModifier FAILED = new QuadModifier(null, false, true);

    private final QuadData data;
    private boolean modified;
    private boolean failed;

    /**
     * @return a {@code QuadModifier} for the given {@link BakedQuad} that can only modify vertex position, texture and normals
     */
    public static QuadModifier of(BakedQuad quad)
    {
        return new QuadModifier(new QuadData(quad), false, false);
    }

    private QuadModifier(QuadData data, boolean modified, boolean failed)
    {
        this.data = data;
        this.modified = modified;
        this.failed = failed;
    }

    /**
     * Apply the given {@link Modifier} to the current vertex data. If a previous modifier failed,
     * then the modification will not be applied
     */
    public QuadModifier apply(Modifier modifier)
    {
        if (!failed)
        {
            failed = !modifier.accept(data);
            modified = true;
        }
        return this;
    }

    /**
     * Re-assemble a copy of the quad and export it to the given quad consumer. If any of the modifiers failed,
     * the quad will not be exported
     */
    public BakedQuad export()
    {
        if (failed)
        {
            return null;
        }

        if (!modified)
        {
            return data.quad;
        }

        return new BakedQuad(
                data.vertexData,
                data.quad.getTintIndex(),
                data.quad.getDirection(),
                data.sprite,
                data.quad.isShade(),
                data.quad.hasAmbientOcclusion()
        );
    }

    /**
     * Clone this {@code QuadModifier} to continue modifying the source quad in multiple different ways without
     * having to repeat the equivalent modification steps
     * @return a new {@code QuadModifier} with a deep-copy of the current data or an empty,
     * failed modifier if this modifier previously failed
     */
    public QuadModifier derive()
    {
        if (failed)
        {
            return FAILED;
        }
        return new QuadModifier(new QuadData(data), modified, false);
    }

    public boolean hasFailed()
    {
        return failed;
    }



    @FunctionalInterface
    public interface Modifier
    {
        boolean accept(QuadData data);
    }
}
