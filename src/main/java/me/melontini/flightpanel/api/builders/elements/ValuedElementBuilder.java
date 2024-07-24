package me.melontini.flightpanel.api.builders.elements;

import java.util.function.Consumer;
import java.util.function.Supplier;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.DataType;
import net.minecraft.text.Text;

public abstract class ValuedElementBuilder<
        T, E extends AbstractValuedElement<T, E>, S extends ValuedElementBuilder<T, E, S>>
    extends BaseElementBuilder<T, E, S> {

  public static final DataType<Object> VALUE = DataType.of();
  public static final DataType<Supplier<Object>> DEFAULT_VALUE = DataType.of();
  public static final DataType<Consumer<Object>> SAVE_FUNCTION = DataType.of();

  protected ValuedElementBuilder(Text elementName, T value) {
    super(elementName);
    value(value);
  }

  public DataType<T> valueType() {
    return (DataType<T>) VALUE;
  }

  public DataType<Supplier<T>> defaultValueType() {
    return (DataType<Supplier<T>>) (Object) DEFAULT_VALUE;
  }

  public DataType<Consumer<T>> saveFunctionType() {
    return (DataType<Consumer<T>>) (Object) SAVE_FUNCTION;
  }

  public S value(T value) {
    return data(valueType(), value);
  }

  public S defaultValue(Supplier<T> supplier) {
    return data(defaultValueType(), supplier);
  }

  public S defaultValue(T value) {
    return defaultValue(() -> value);
  }

  public S saveFunction(Consumer<T> function) {
    return data(saveFunctionType(), function);
  }
}
