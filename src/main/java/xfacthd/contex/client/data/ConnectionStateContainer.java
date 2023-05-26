package xfacthd.contex.client.data;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import xfacthd.contex.api.state.ConnectionState;

import java.util.*;

public final class ConnectionStateContainer // TODO: check hashing performance
{
    private final StateMap[] states = new StateMap[6];

    public StateMap get(Direction side)
    {
        return states[side.ordinal()];
    }

    public StateMap getOrCreate(Direction side)
    {
        StateMap map = states[side.ordinal()];
        if (map == null)
        {
            states[side.ordinal()] = map = new StateMap();
        }
        return map;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(states);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        return Arrays.equals(states, ((ConnectionStateContainer) o).states);
    }



    public static final class StateMap
    {
        private final Map<ResourceLocation, ConnectionState> stateMap = new HashMap<>();
        private final List<ConnectionState> states = new ArrayList<>();

        public void put(ResourceLocation tex, ConnectionState state)
        {
            stateMap.put(tex, state);
            states.add(state);
        }

        public ConnectionState get(ResourceLocation tex)
        {
            return stateMap.get(tex);
        }

        @Override
        public int hashCode()
        {
            return states.hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            return Objects.equals(states, ((StateMap) o).states);
        }
    }
}
