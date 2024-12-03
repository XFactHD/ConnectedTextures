package xfacthd.contex.client.loader;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.model.DelegateUnbakedModel;
import xfacthd.contex.client.data.MetaEntry;
import xfacthd.contex.client.model.ConTexModel;

import java.util.List;

public final class UnbakedConTexModel extends DelegateUnbakedModel
{
    private final List<MetaEntry> metadata;

    public UnbakedConTexModel(UnbakedModel baseModel, List<MetaEntry> metadata)
    {
        super(baseModel);
        this.metadata = metadata;
    }

    @Override
    public BakedModel bake(
            TextureSlots textures,
            ModelBaker baker,
            ModelState modelState,
            boolean useAmbientOcclusion,
            boolean usesBlockLight,
            ItemTransforms itemTransforms,
            ContextMap additionalProperties
    )
    {
        BakedModel bakedBase = wrapped.bake(textures, baker, modelState, useAmbientOcclusion, usesBlockLight, itemTransforms, additionalProperties);
        return metadata.isEmpty() ? bakedBase : new ConTexModel(bakedBase, metadata);
    }
}
