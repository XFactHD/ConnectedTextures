package io.github.xfacthd.contex.api.model;

import io.github.xfacthd.contex.api.state.ConnectionDirection;
import io.github.xfacthd.contex.api.utils.Utils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.jspecify.annotations.Nullable;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public final class QuadRebaker {
    private static final Deque<MutableQuad> POOL = new ConcurrentLinkedDeque<>();
    private static final float WIDTH = .5F;

    /// Cuts the provided quad down into the quarter declared by the two provided [ConnectionDirection]s.
    ///
    /// @param quad         The quad to process
    /// @param uDir         The horizontal direction of the corner the resulting quad will cover
    /// @param vDir         The vertical direction of the corner the resulting quad will cover
    /// @param targetSprite The sprite to apply to the rebaked quad or `null` to keep the current sprite
    /// @param output       The output to pass the resulting quad to
    public static void process(BakedQuad quad, ConnectionDirection uDir, ConnectionDirection vDir, @Nullable TextureAtlasSprite targetSprite, Consumer<BakedQuad> output) {
        MutableQuad mutQuad = acquireQuad();
        mutQuad.setFrom(quad);
        boolean uvRotated = isUvRotated(mutQuad);
        Direction quadDir = quad.direction();
        if (cutEdge(mutQuad, quadDir, uDir, uvRotated) && cutEdge(mutQuad, quadDir, vDir, uvRotated)) {
            if (targetSprite != null) {
                remapSprite(mutQuad, targetSprite);
            }
            output.accept(mutQuad.toBakedQuad());
        }
        releaseQuad(mutQuad);
    }

    public static void processRemapOnly(BakedQuad quad, TextureAtlasSprite targetSprite, Consumer<BakedQuad> output) {
        MutableQuad mutQuad = acquireQuad();
        mutQuad.setFrom(quad);
        remapSprite(mutQuad, targetSprite);
        output.accept(mutQuad.toBakedQuad());
        releaseQuad(mutQuad);
    }

    private static boolean cutEdge(MutableQuad quad, Direction quadDir, ConnectionDirection cutDir, boolean uvRotated) {
        Direction cutEdge = cutDir.toCutEdge(quadDir);
        CuttingConfig config = CuttingConfig.get(quadDir, cutEdge);
        boolean positive = Utils.isPositive(cutEdge);
        int coordForward = config.forwardCoord();

        CuttingConfig.VertPair checkPair = config.checkEdgeVerts();
        float checkCoordOne = quad.positionComponent(checkPair.v1(), coordForward);
        float checkCoordTwo = quad.positionComponent(checkPair.v2(), coordForward);
        if (positive && (Utils.isHigher(checkCoordOne, WIDTH) || Utils.isHigher(checkCoordTwo, WIDTH))) {
            return false;
        }
        if (!positive && (Utils.isLower(checkCoordOne, WIDTH) || Utils.isLower(checkCoordTwo, WIDTH))) {
            return false;
        }

        CuttingConfig.VertPair cutPair = config.cutEdgeVerts();

        float posOne = quad.positionComponent(cutPair.v1(), coordForward);
        float posTwo = quad.positionComponent(cutPair.v2(), coordForward);
        if (Mth.equal(posOne, WIDTH) && Mth.equal(posTwo, WIDTH)) {
            return true;
        }

        boolean vAxis = config.vAxis();
        remapUV(quad, config.uvVertsOne(), coordForward, cutPair.v1(), vAxis, uvRotated);
        remapUV(quad, config.uvVertsTwo(), coordForward, cutPair.v2(), vAxis, uvRotated);

        quad.setPositionComponent(cutPair.v1(), coordForward, WIDTH);
        quad.setPositionComponent(cutPair.v2(), coordForward, WIDTH);

        return true;
    }

    private static void remapUV(MutableQuad quad, CuttingConfig.UvSrcVertSet uvVerts, int coordForward, int uvTo, boolean vAxis, boolean uvRotated) {
        float coord1 = quad.positionComponent(uvVerts.posOne(), coordForward);
        float coord2 = quad.positionComponent(uvVerts.posTwo(), coordForward);
        float coordMin = Math.min(coord1, coord2);
        float coordMax = Math.max(coord1, coord2);

        int uvIdx = uvRotated != vAxis ? 1 : 0;
        float uvAbs1 = quad.uvComponent(uvVerts.uvOne(), uvIdx);
        float uvAbs2 = quad.uvComponent(uvVerts.uvTwo(), uvIdx);
        float uvAbsMin = Math.min(uvAbs1, uvAbs2);
        float uvAbsMax = Math.max(uvAbs1, uvAbs2);
        boolean invert = ((coord2 > coord1) ^ (uvAbs2 > uvAbs1)) != vAxis;

        if (WIDTH == coordMin) {
            quad.setUvComponent(uvTo, uvIdx, invert ? uvAbsMax : uvAbsMin);
        } else if (WIDTH == coordMax) {
            quad.setUvComponent(uvTo, uvIdx, invert ? uvAbsMin : uvAbsMax);
        } else {
            float mult = (WIDTH - coordMin) / (coordMax - coordMin);
            if (invert) { mult = 1F - mult; }
            quad.setUvComponent(uvTo, uvIdx, Mth.lerp(mult, uvAbsMin, uvAbsMax));
        }
    }

    private static void remapSprite(MutableQuad quad, TextureAtlasSprite targetSprite) {
        TextureAtlasSprite srcSprite = quad.requiredSprite();

        for (int i = 0; i < 4; i++) {
            float uRel = getRelUV(quad, srcSprite, i, 0);
            float vRel = getRelUV(quad, srcSprite, i, 1);
            quad.setUv(i, targetSprite.getU(uRel), targetSprite.getV(vRel));
        }

        quad.setSprite(targetSprite, quad.requiredChunkLayer(), quad.requiredItemRenderType());
    }

    private static float getRelUV(MutableQuad quad, TextureAtlasSprite sprite, int vertex, int uvIdx) {
        float uv0 = uvIdx == 0 ? sprite.getU0() : sprite.getV0();
        float uv1 = uvIdx == 0 ? sprite.getU1() : sprite.getV1();
        return (quad.uvComponent(vertex, uvIdx) - uv0) / (uv1 - uv0);
    }

    private static boolean isUvRotated(MutableQuad quad) {
        return (Mth.equal(quad.uvComponent(0, 1), quad.uvComponent(1, 1)) || Mth.equal(quad.uvComponent(3, 1), quad.uvComponent(2, 1))) &&
               (Mth.equal(quad.uvComponent(1, 0), quad.uvComponent(2, 0)) || Mth.equal(quad.uvComponent(0, 0), quad.uvComponent(3, 0)));
    }

    private static MutableQuad acquireQuad() {
        MutableQuad quad = POOL.pollFirst();
        if (quad == null) {
            quad = new MutableQuad();
        }
        return quad;
    }

    private static void releaseQuad(MutableQuad quad) {
        POOL.addLast(quad);
    }

    private QuadRebaker() { }
}
