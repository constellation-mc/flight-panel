package me.melontini.flightpanel.api.builders.elements.collections;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import me.melontini.flightpanel.impl.elements.collections.ListButtonElement;
import net.minecraft.text.Text;

public class ListButtonBuilder<T>
    extends ListBasedBuilder<T, ListButtonElement<T>, ListButtonBuilder<T>> {

  public static <T extends Enum<T>> ListButtonBuilder<T> forEnum(
      Text elementName, T value, Function<T, Text> textifier) {
    return new ListButtonBuilder<>(
        elementName, value, Arrays.asList(value.getDeclaringClass().getEnumConstants()), textifier);
  }

  public static <T> ListButtonBuilder<T> create(
      Text elementName, T value, List<T> values, Function<T, Text> textifier) {
    return new ListButtonBuilder<>(elementName, value, values, textifier);
  }

  protected ListButtonBuilder(
      Text elementName, T value, List<T> values, Function<T, Text> textifier) {
    super(elementName, value, values, textifier);
  }

  @Override
  public ListButtonElement<T> build() {
    return new ListButtonElement<>(this);
  }
}
