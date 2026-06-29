package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.FloatTextBoxElement;
import net.minecraft.network.chat.Component;

public class FloatTextBoxBuilder
    extends RangedNumberElementBuilder<Float, FloatTextBoxElement, FloatTextBoxBuilder> {

  public static FloatTextBoxBuilder create(Component elementName, float value) {
    return new FloatTextBoxBuilder(elementName, value);
  }

  protected FloatTextBoxBuilder(Component elementName, float value) {
    super(elementName, value);
  }

  @Override
  public FloatTextBoxElement build() {
    return new FloatTextBoxElement(this);
  }
}
