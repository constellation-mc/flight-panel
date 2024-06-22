package me.melontini.flightpanel.api.builders.elements;

import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.DataType;
import me.melontini.flightpanel.impl.elements.NestedListElement;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NestedListBuilder<T> extends ValuedElementBuilder<List<T>, NestedListElement<T>, NestedListBuilder<T>> {

    public static final DataType<BiFunction<Object, NestedListElement<Object>, AbstractValuedElement<Object, ?>>> CELL_FACTORY = DataType.of();
    public static final DataType<Supplier<Object>> DEFAULT_ELEMENT = DataType.of();
    public static final DataType<Boolean> IMMUTABLE = DataType.of();

    public DataType<BiFunction<T, NestedListElement<T>, AbstractValuedElement<T, ?>>> cellFactoryType() {
        return (DataType<BiFunction<T, NestedListElement<T>, AbstractValuedElement<T, ?>>>) (Object) CELL_FACTORY;
    }
    public DataType<Supplier<T>> defaultElementType() {
        return (DataType<Supplier<T>>) (Object) DEFAULT_ELEMENT;
    }

    public static <T> NestedListBuilder<T> create(Text elementName, List<T> value, BiFunction<T, NestedListElement<T>, AbstractValuedElement<T, ?>> cellFactory, Supplier<T> defaultElementValue) {
        return new NestedListBuilder<>(elementName, value, cellFactory, defaultElementValue);
    }

    protected NestedListBuilder(Text elementName, List<T> value, BiFunction<T, NestedListElement<T>, AbstractValuedElement<T, ?>> cellFactory, Supplier<T> defaultElementValue) {
        super(elementName, value);
        this.data(cellFactoryType(), cellFactory);
        this.data(defaultElementType(), defaultElementValue);
    }

    public NestedListBuilder<T> immutable(boolean value) {
        return this.data(IMMUTABLE, value);
    }
    public NestedListBuilder<T> immutable() {
        return this.immutable(true);
    }

    public NestedListBuilder<T> collapsed(boolean value) {
        return this.data(CollapsibleObjectBuilder.COLLAPSED, value);
    }
    public NestedListBuilder<T> collapsed() {
        return this.collapsed(true);
    }
    public NestedListBuilder<T> expanded() {
        return this.collapsed(false);
    }

    @Override
    public NestedListElement<T> build() {
        return new NestedListElement<>(this);
    }
}
