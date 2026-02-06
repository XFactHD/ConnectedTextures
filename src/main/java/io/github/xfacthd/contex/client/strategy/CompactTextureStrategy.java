package io.github.xfacthd.contex.client.strategy;

import io.github.xfacthd.contex.api.model.QuadRebaker;
import io.github.xfacthd.contex.api.model.SpriteLookup;
import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.type.SpriteType;
import io.github.xfacthd.contex.api.type.TextureStrategy;
import io.github.xfacthd.contex.api.type.TextureType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;

import java.util.Set;
import java.util.function.Consumer;

public final class CompactTextureStrategy implements TextureStrategy
{
    public static final CompactTextureStrategy INSTANCE = new CompactTextureStrategy();

    @Override
    public void makeConnectionQuads(TextureType type, BakedQuad srcQuad, Direction side, byte state, SpriteLookup sprites, Consumer<BakedQuad> output)
    {
        makeConnectionQuad(type, srcQuad, side, state, ConnectionDirection.LEFT, ConnectionDirection.UP, sprites, output);
        makeConnectionQuad(type, srcQuad, side, state, ConnectionDirection.RIGHT, ConnectionDirection.UP, sprites, output);
        makeConnectionQuad(type, srcQuad, side, state, ConnectionDirection.LEFT, ConnectionDirection.DOWN, sprites, output);
        makeConnectionQuad(type, srcQuad, side, state, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, sprites, output);
    }

    private static void makeConnectionQuad(
            TextureType type,
            BakedQuad srcQuad,
            Direction side,
            byte state,
            ConnectionDirection uDir,
            ConnectionDirection vDir,
            SpriteLookup sprites,
            Consumer<BakedQuad> output
    )
    {
        boolean xCon = uDir.isSet(state);
        boolean yCon = vDir.isSet(state);

        TextureAtlasSprite targetSprite = null;
        if (xCon || yCon)
        {
            boolean diagCon = ConnectionDirection.diagonal(uDir, vDir).isSet(state);
            targetSprite = sprites.get(type.getConnectedSprite(xCon, yCon, diagCon, side));
        }
        QuadRebaker.process(srcQuad, uDir, vDir, targetSprite, output);
    }

    @Override
    public void makeNonCtQuads(BakedQuad srcQuad, Consumer<BakedQuad> output)
    {
        QuadRebaker.process(srcQuad, ConnectionDirection.RIGHT, ConnectionDirection.UP, null, output);
        QuadRebaker.process(srcQuad, ConnectionDirection.RIGHT, ConnectionDirection.DOWN, null, output);
        QuadRebaker.process(srcQuad, ConnectionDirection.LEFT, ConnectionDirection.UP, null, output);
        QuadRebaker.process(srcQuad, ConnectionDirection.LEFT, ConnectionDirection.DOWN, null, output);
    }

    @Override
    public Set<SpriteType> computePermittedTypes(Set<SpriteType> spriteTypes)
    {
        return spriteTypes;
    }
}
