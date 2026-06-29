package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.LongTextBoxElement;
import net.minecraft.network.chat.Component;

public class LongTextBoxBuilder
    extends RangedNumberElementBuilder<Long, LongTextBoxElement, LongTextBoxBuilder> {

  public static LongTextBoxBuilder create(Component elementName, long value) {
    return new LongTextBoxBuilder(elementName, value);
  }

  protected LongTextBoxBuilder(Component elementName, long value) {
    super(elementName, value);
  }

  @Override
  public LongTextBoxElement build() {
    return new LongTextBoxElement(this);
  }
}
