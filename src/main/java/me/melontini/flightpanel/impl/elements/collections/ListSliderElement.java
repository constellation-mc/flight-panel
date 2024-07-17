package me.melontini.flightpanel.impl.elements.collections;

import me.melontini.flightpanel.api.builders.elements.collections.ListSliderBuilder;
import me.melontini.flightpanel.api.elements.AbstractSliderElement;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

public class ListSliderElement<T> extends AbstractSliderElement<T, ListSliderElement<T>> {

    private final List<T> values;
    private final Function<T, Text> textifier;

    public ListSliderElement(ListSliderBuilder<T> builder) {
        super(builder);
        this.values = builder.dataOrThrow(builder.valuesType());
        this.textifier = builder.dataOrThrow(builder.textifierType());
        this.applyDefaults();
    }

    @Override
    protected Text getMessage(T value) {
        return this.textifier.apply(value);
    }

    @Override
    protected T convertFromRange(double value) {
        return values.get((int) Math.round((values.size() - 1) * value));
    }

    @Override
    protected double convertToRange(T value) {
        return (double) values.indexOf(value) / (values.size() - 1);
    }
}
