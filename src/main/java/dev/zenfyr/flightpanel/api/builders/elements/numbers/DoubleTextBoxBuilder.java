package dev.zenfyr.flightpanel.api.builders.elements.numbers;

import dev.zenfyr.flightpanel.impl.elements.numbers.DoubleTextBoxElement;
import net.minecraft.network.chat.Component;

public class DoubleTextBoxBuilder
    extends RangedNumberElementBuilder<Double, DoubleTextBoxElement, DoubleTextBoxBuilder> {

  public static DoubleTextBoxBuilder create(Component elementName, double value) {
    return new DoubleTextBoxBuilder(elementName, value);
  }

  protected DoubleTextBoxBuilder(Component elementName, double value) {
    super(elementName, value);
  }

  @Override
  public DoubleTextBoxElement build() {
    return new DoubleTextBoxElement(this);
  }
}
