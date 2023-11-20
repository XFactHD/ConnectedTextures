package xfacthd.contex.client.loader;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import xfacthd.contex.client.data.Metadata;
import xfacthd.contex.client.model.ConTexModel;
import xfacthd.contex.client.model.SingleConTexModel;

import java.util.function.Function;

public record ConTexGeometry(UnbakedModel baseModel, Metadata metadata) implements IUnbakedGeometry<ConTexGeometry>
{
    @Override
    public BakedModel bake(
            IGeometryBakingContext ctx,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState,
            ItemOverrides overrides,
            ResourceLocation location
    )
    {
        BakedModel bakedBase = baseModel.bake(baker, spriteGetter, modelState, location);
        return switch (metadata.entries().size())
        {
            case 0 -> bakedBase;
            case 1 -> new SingleConTexModel(bakedBase, metadata);
            default -> new ConTexModel(bakedBase, metadata);
        };
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveParents(modelGetter);
    }
}
