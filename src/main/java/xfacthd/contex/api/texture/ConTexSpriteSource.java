package xfacthd.contex.api.texture;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import xfacthd.contex.client.texture.ConTexSpriteSupplier;

import java.util.Optional;

public record ConTexSpriteSource(ResourceLocation texture, Border border, Optional<ResourceLocation> sprite) implements SpriteSource
{
    public static final MapCodec<ConTexSpriteSource> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ConTexSpriteSource::texture),
            Border.CODEC.fieldOf("border").forGetter(ConTexSpriteSource::border),
            ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter(ConTexSpriteSource::sprite)
    ).apply(inst, ConTexSpriteSource::new));

    @Override
    public void run(ResourceManager resourceManager, Output output)
    {
        ResourceLocation texLoc = TEXTURE_ID_CONVERTER.idToFile(texture);
        Optional<Resource> resource = resourceManager.getResource(texLoc);
        if (resource.isEmpty())
        {
            LogUtils.getLogger().warn("Missing sprite: {}", texture);
            return;
        }

        ResourceLocation outLoc = sprite.isPresent() ? sprite.get() : texture.withSuffix("_ctm");
        output.add(outLoc, new ConTexSpriteSupplier(texture, outLoc, resource.get(), border));
    }

    @Override
    public MapCodec<? extends SpriteSource> codec()
    {
        return CODEC;
    }
}
