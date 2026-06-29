package dev.zenfyr.flightpanel.api.builders.elements.numbers;

import dev.zenfyr.flightpanel.impl.elements.numbers.IntTextBoxElement;
import net.minecraft.network.chat.Component;

public class IntTextBoxBuilder
    extends RangedNumberElementBuilder<Integer, IntTextBoxElement, IntTextBoxBuilder> {

  public static IntTextBoxBuilder create(Component elementName, int value) {
    return new IntTextBoxBuilder(elementName, value);
  }

  protected IntTextBoxBuilder(Component elementName, int value) {
    super(elementName, value);
  }

  @Override
  public IntTextBoxElement build() {
    return new IntTextBoxElement(this);
  }
}
