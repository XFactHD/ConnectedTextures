package xfacthd.contex.api.texture;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import xfacthd.contex.client.texture.ConTexSpriteSupplier;

import java.util.Optional;
import java.util.Set;

public record ConTexSpriteSource(Identifier texture, Border border, Optional<Identifier> sprite) implements SpriteSource
{
    public static final MapCodec<ConTexSpriteSource> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("texture").forGetter(ConTexSpriteSource::texture),
            Border.CODEC.fieldOf("border").forGetter(ConTexSpriteSource::border),
            Identifier.CODEC.optionalFieldOf("sprite").forGetter(ConTexSpriteSource::sprite)
    ).apply(inst, ConTexSpriteSource::new));

    @Override
    public void run(ResourceManager resourceManager, Output output)
    {
        run(resourceManager, output, Set.of());
    }

    @Override
    public void run(ResourceManager resourceManager, Output output, Set<MetadataSectionType<?>> additionalMetadata)
    {
        Identifier texLoc = TEXTURE_ID_CONVERTER.idToFile(texture);
        Optional<Resource> resource = resourceManager.getResource(texLoc);
        if (resource.isEmpty())
        {
            LogUtils.getLogger().warn("Missing sprite: {}", texture);
            return;
        }

        Identifier outLoc = sprite.isPresent() ? sprite.get() : texture.withSuffix("_ctm");
        output.add(outLoc, new ConTexSpriteSupplier(texture, outLoc, resource.get(), border, additionalMetadata));
    }

    @Override
    public MapCodec<? extends SpriteSource> codec()
    {
        return CODEC;
    }
}
