package me.melontini.flightpanel.api.builders.elements.collections;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import me.melontini.flightpanel.impl.elements.collections.ListSliderElement;
import net.minecraft.network.chat.Component;

public class ListSliderBuilder<T>
    extends ListBasedBuilder<T, ListSliderElement<T>, ListSliderBuilder<T>> {

  public static <T extends Enum<T>> ListSliderBuilder<T> forEnum(
      Component elementName, T value, Function<T, Component> textifier) {
    return new ListSliderBuilder<>(
        elementName, value, Arrays.asList(value.getDeclaringClass().getEnumConstants()), textifier);
  }

  public static <T> ListSliderBuilder<T> create(
      Component elementName, T value, List<T> values, Function<T, Component> textifier) {
    return new ListSliderBuilder<>(elementName, value, values, textifier);
  }

  protected ListSliderBuilder(
      Component elementName, T value, List<T> values, Function<T, Component> textifier) {
    super(elementName, value, values, textifier);
  }

  @Override
  public ListSliderElement<T> build() {
    return new ListSliderElement<>(this);
  }
}
