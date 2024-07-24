package me.melontini.flightpanel.api.builders.elements.collections;

import java.util.List;
import java.util.function.Function;
import me.melontini.flightpanel.api.builders.elements.ValuedElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.DataType;
import net.minecraft.text.Text;

public abstract class ListBasedBuilder<
        T, E extends AbstractValuedElement<T, E>, S extends ListBasedBuilder<T, E, S>>
    extends ValuedElementBuilder<T, E, S> {

  public static final DataType<List<?>> VALUES = DataType.of();
  public static final DataType<Function<?, Text>> TEXTIFIER = DataType.of();

  public DataType<List<T>> valuesType() {
    return (DataType<List<T>>) (Object) VALUES;
  }

  public DataType<Function<T, Text>> textifierType() {
    return (DataType<Function<T, Text>>) (Object) TEXTIFIER;
  }

  protected ListBasedBuilder(
      Text elementName, T value, List<T> values, Function<T, Text> textifier) {
    super(elementName, value);
    this.data(valuesType(), values);
    this.data(textifierType(), textifier);
  }
}
