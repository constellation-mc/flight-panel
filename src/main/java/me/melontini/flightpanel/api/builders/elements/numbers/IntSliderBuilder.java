package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.IntSliderElement;
import net.minecraft.network.chat.Component;

public class IntSliderBuilder
    extends RangedNumberElementBuilder<Integer, IntSliderElement, IntSliderBuilder> {

  public static IntSliderBuilder create(Component elementName, int value, int min, int max) {
    return new IntSliderBuilder(elementName, value, min, max);
  }

  protected IntSliderBuilder(Component elementName, int value, int min, int max) {
    super(elementName, value);
    this.min(min);
    this.max(max);
  }

  @Override
  public IntSliderElement build() {
    return new IntSliderElement(this);
  }
}
