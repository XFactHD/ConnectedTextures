package io.github.xfacthd.contex.api.datagen;

import com.google.common.base.Preconditions;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.client.data.TextureEntry;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;

import java.util.Set;

public final class TextureEntryBuilder {
    private final TextureType textureType;
    private final TextureStrategy strategy;
    private final Set<SpriteType> permittedSpriteTypes;
    private final Identifier baseTexture;
    private final Reference2ObjectMap<SpriteType, Identifier> ctTextures = new Reference2ObjectOpenHashMap<>();

    TextureEntryBuilder(TextureType textureType, TextureStrategy strategy, Identifier baseTexture) {
        this.textureType = textureType;
        this.strategy = strategy;
        this.permittedSpriteTypes = strategy.computePermittedTypes(textureType.getSpriteTypes());
        this.baseTexture = baseTexture;
    }

    public TextureEntryBuilder addTexture(SpriteType type, Identifier texture) {
        Preconditions.checkArgument(permittedSpriteTypes.contains(type), "SpriteType %s is unsupported by TextureType %s with TextureStrategy %s", type, textureType, strategy);
        ctTextures.put(type, texture);
        return this;
    }

    TextureEntry build() {
        return new TextureEntry(baseTexture, ctTextures);
    }
}
