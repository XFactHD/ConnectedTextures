package xfacthd.contex.client.loader;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import xfacthd.contex.client.data.MetaEntry;
import xfacthd.contex.client.model.ConTexModel;

import java.util.List;
import java.util.function.Function;

public record ConTexGeometry(UnbakedModel baseModel, List<MetaEntry> metadata) implements IUnbakedGeometry<ConTexGeometry>
{
    @Override
    public BakedModel bake(
            IGeometryBakingContext ctx,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState,
            List<ItemOverride> overrides
    )
    {
        BakedModel bakedBase = baseModel.bake(baker, spriteGetter, modelState);
        return metadata.isEmpty() ? bakedBase : new ConTexModel(bakedBase, metadata);
    }

    @Override
    public void resolveDependencies(UnbakedModel.Resolver modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveDependencies(modelGetter);
    }
}
