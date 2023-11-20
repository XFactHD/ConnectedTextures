package xfacthd.contex.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.contex.api.state.ConnectionState;
import xfacthd.contex.api.utils.Constants;
import xfacthd.contex.client.data.*;

import java.util.ArrayList;
import java.util.List;

public final class SingleConTexModel extends ConTexModel
{
    private final TextureEntry texture;

    public SingleConTexModel(BakedModel baseModel, Metadata metadata)
    {
        super(baseModel, metadata);
        this.texture = metadata.getAll().stream().findFirst().orElseThrow();
    }

    @Override
    protected List<BakedQuad> generateConnectionQuads(
            ConnectionStateContainer.StateMap ctStates, List<BakedQuad> inputQuads, @Nullable Direction side
    )
    {
        ConnectionState conState = ctStates.get(texture.baseTexture());
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad quad : inputQuads)
        {
            ResourceLocation tex = quad.getSprite().contents().name();
            collectQuads(quads, quad, tex, side, texture, conState);
        }
        return quads;
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
    {
        ConnectionStateContainer ctState = new ConnectionStateContainer();
        collectStateForEntry(level, pos, state, ctState, texture);
        return modelData.derive().with(Constants.CT_STATE_PROPERTY, ctState).build();
    }
}
