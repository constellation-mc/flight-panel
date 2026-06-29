package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.LongSliderElement;
import net.minecraft.network.chat.Component;

public class LongSliderBuilder
    extends RangedNumberElementBuilder<Long, LongSliderElement, LongSliderBuilder> {

  public static LongSliderBuilder create(Component elementName, long value, long min, long max) {
    return new LongSliderBuilder(elementName, value, min, max);
  }

  protected LongSliderBuilder(Component elementName, long value, long min, long max) {
    super(elementName, value);
    this.min(min);
    this.max(max);
  }

  @Override
  public LongSliderElement build() {
    return new LongSliderElement(this);
  }
}
