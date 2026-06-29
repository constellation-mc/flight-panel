package dev.zenfyr.flightpanel.api.builders.elements.collections;

import dev.zenfyr.flightpanel.impl.elements.collections.ListButtonElement;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class ListButtonBuilder<T>
    extends ListBasedBuilder<T, ListButtonElement<T>, ListButtonBuilder<T>> {

  public static <T extends Enum<T>> ListButtonBuilder<T> forEnum(
      Component elementName, T value, Function<T, Component> textifier) {
    return new ListButtonBuilder<>(
        elementName, value, Arrays.asList(value.getDeclaringClass().getEnumConstants()), textifier);
  }

  public static <T> ListButtonBuilder<T> create(
      Component elementName, T value, List<T> values, Function<T, Component> textifier) {
    return new ListButtonBuilder<>(elementName, value, values, textifier);
  }

  protected ListButtonBuilder(
      Component elementName, T value, List<T> values, Function<T, Component> textifier) {
    super(elementName, value, values, textifier);
  }

  @Override
  public ListButtonElement<T> build() {
    return new ListButtonElement<>(this);
  }
}
