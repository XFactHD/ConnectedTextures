package io.github.xfacthd.contex.client.data;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Optional;

public record StatePredicate(Map<String, String> properties) {
    public static final Codec<StatePredicate> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
            .xmap(StatePredicate::new, StatePredicate::properties);

    public boolean matches(BlockState state) {
        StateDefinition<Block, BlockState> definition = state.getBlock().getStateDefinition();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Property<?> property = definition.getProperty(entry.getKey());
            if (property == null) {
                return false;
            }
            Optional<?> value = property.getValue(entry.getValue());
            if (value.isEmpty() || value.get() != state.getValue(property)) {
                return false;
            }
        }
        return true;
    }
}
