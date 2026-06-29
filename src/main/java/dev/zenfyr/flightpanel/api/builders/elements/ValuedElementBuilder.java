package dev.zenfyr.flightpanel.api.builders.elements;

import dev.zenfyr.flightpanel.api.elements.AbstractValuedElement;
import dev.zenfyr.flightpanel.api.util.DataType;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public abstract class ValuedElementBuilder<
        T, E extends AbstractValuedElement<T, E>, S extends ValuedElementBuilder<T, E, S>>
    extends BaseElementBuilder<T, E, S> {

  public static final DataType<Object> VALUE = DataType.of();
  public static final DataType<Supplier<Object>> DEFAULT_VALUE = DataType.of();
  public static final DataType<Consumer<Object>> SAVE_FUNCTION = DataType.of();

  protected ValuedElementBuilder(Component elementName, T value) {
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
