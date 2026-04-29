package io.github.xfacthd.contex.api.type;

import io.github.xfacthd.contex.api.model.SpriteLookup;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;

import java.util.Set;
import java.util.function.Consumer;

public interface TextureStrategy {
    /// Create the [BakedQuad]s making up the given {@linkplain Direction side} of the block with the given connection state.
    /// Must only be called for sides contained in the set returned by [TextureType#getAffectedFaces()].
    ///
    /// @param srcQuad The original quad on the given side
    /// @param side    The side of the block
    /// @param state   The calculated connection state
    /// @param sprites The additional textures to use for connections
    /// @param output  The output to pass the generated quads to
    void makeConnectionQuads(TextureType type, BakedQuad srcQuad, Direction side, byte state, SpriteLookup sprites, Consumer<BakedQuad> output);

    /// Adjust the provided non-CT quad to ensure it does not z-fight with any CT quads.
    ///
    /// @param srcQuad The original quad on the given side
    /// @param output  The output to pass the generated quads to
    void makeNonCtQuads(BakedQuad srcQuad, Consumer<BakedQuad> output);

    /// Compute the permitted [SpriteType]s from the [SpriteType]s declared by the [TextureType].
    ///
    /// @param spriteTypes The [SpriteType]s declared by the [TextureType]
    /// @return The permitted [SpriteType]s
    Set<SpriteType> computePermittedTypes(Set<SpriteType> spriteTypes);
}
