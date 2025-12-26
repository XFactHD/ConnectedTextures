package io.github.xfacthd.contex.api.datagen;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;
import io.github.xfacthd.contex.api.type.ConnectionPredicate;
import io.github.xfacthd.contex.api.type.OcclusionMode;
import io.github.xfacthd.contex.api.type.TextureType;
import io.github.xfacthd.contex.client.data.MetaEntry;
import io.github.xfacthd.contex.client.data.StatePredicate;
import io.github.xfacthd.contex.client.data.TextureEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class MetaEntryBuilder
{
    private final TextureType type;
    @Nullable
    private ConnectionPredicate predicate;
    private OcclusionMode occlusionMode = OcclusionMode.SELF;
    private final Map<String, String> statePredicateProperties = new HashMap<>();
    private final List<TextureEntry> textures = new ArrayList<>();

    MetaEntryBuilder(TextureType type)
    {
        this.type = type;
    }

    public MetaEntryBuilder predicate(ConnectionPredicate predicate)
    {
        this.predicate = predicate;
        return this;
    }

    public MetaEntryBuilder occlusionMode(OcclusionMode occlusionMode)
    {
        this.occlusionMode = occlusionMode;
        return this;
    }

    public <T extends Comparable<T>> MetaEntryBuilder addStateFilter(Property<T> property, T value)
    {
        statePredicateProperties.put(property.getName(), property.getName(value));
        return this;
    }

    public MetaEntryBuilder addTexture(Identifier texture)
    {
        return addTexture(texture, UnaryOperator.identity());
    }

    public MetaEntryBuilder addTexture(Identifier baseTexture, UnaryOperator<TextureEntryBuilder> consumer)
    {
        textures.add(consumer.apply(new TextureEntryBuilder(type, baseTexture)).build());
        return this;
    }

    MetaEntry build()
    {
        Objects.requireNonNull(type, "No TextureType specified");
        Objects.requireNonNull(predicate, "No ConnectionPredicate specified");
        Optional<StatePredicate> statePredicate = Optional.empty();
        if (!statePredicateProperties.isEmpty())
        {
            statePredicate = Optional.of(new StatePredicate(statePredicateProperties));
        }
        return new MetaEntry(type, predicate, occlusionMode, statePredicate, textures.toArray(TextureEntry[]::new));
    }
}
