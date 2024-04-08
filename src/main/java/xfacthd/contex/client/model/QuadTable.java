package xfacthd.contex.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Arrays;

public final class QuadTable
{
    private static final int LAYER_COUNT = RenderType.chunkBufferLayers().size();
    private static final int SIDE_COUNT = Direction.values().length;
    private static final ArrayList<Entry> EMPTY = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private final ArrayList<Entry>[] quads = new ArrayList[LAYER_COUNT * SIDE_COUNT];

    public QuadTable()
    {
        Arrays.fill(quads, EMPTY);
    }

    public ArrayList<Entry> get(Direction side, RenderType renderType)
    {
        return quads[renderType.getChunkLayerId() * SIDE_COUNT + side.ordinal()];
    }

    public void put(Direction side, RenderType renderType, ArrayList<Entry> quadList)
    {
        quads[renderType.getChunkLayerId() * SIDE_COUNT + side.ordinal()] = quadList;
    }

    public record Entry(BakedQuad quad, int metaIdx, int texIdx) { }
}
