package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.api.builders.elements.ValuedElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.DataType;
import net.minecraft.text.Text;

public abstract class RangedNumberElementBuilder<T extends Number, E extends AbstractValuedElement<T, E>, S extends RangedNumberElementBuilder<T, E, S>> extends ValuedElementBuilder<T, E, S> {

    public static final DataType<Number> MIN = DataType.of();
    public static final DataType<Number> MAX = DataType.of();

    protected RangedNumberElementBuilder(Text elementName, T value) {
        super(elementName, value);
    }

    public DataType<T> minType() {
        return (DataType<T>) MIN;
    }
    public DataType<T> maxType() {
        return (DataType<T>) MAX;
    }

    public S min(T min) {
        return data(minType(), min);
    }
    public S max(T max) {
        return data(maxType(), max);
    }
}
