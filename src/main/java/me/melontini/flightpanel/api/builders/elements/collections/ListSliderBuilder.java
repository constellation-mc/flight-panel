package me.melontini.flightpanel.api.builders.elements.collections;

import me.melontini.flightpanel.impl.elements.collections.ListSliderElement;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

public class ListSliderBuilder<T> extends ListBasedBuilder<T, ListSliderElement<T>, ListSliderBuilder<T>> {

    public static <T> ListSliderBuilder<T> create(Text elementName, T value, List<T> values, Function<T, Text> textifier) {
        return new ListSliderBuilder<>(elementName, value, values, textifier);
    }

    protected ListSliderBuilder(Text elementName, T value, List<T> values, Function<T, Text> textifier) {
        super(elementName, value, values, textifier);
    }

    @Override
    public ListSliderElement<T> build() {
        return new ListSliderElement<>(this);
    }
}
