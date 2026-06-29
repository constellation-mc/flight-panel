package dev.zenfyr.flightpanel.impl.elements.numbers;

import dev.zenfyr.flightpanel.api.builders.elements.numbers.IntSliderBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberSliderElement;
import net.minecraft.network.chat.Component;

public class IntSliderElement extends AbstractNumberSliderElement<Integer, IntSliderElement> {

  public IntSliderElement(IntSliderBuilder builder) {
    super(builder);
    this.applyDefaults();
  }

  @Override
  protected Component getMessage(Integer value) {
    return Component.literal(Integer.toString(value));
  }

  @Override
  protected Integer convertFromRange(double value) {
    return (int) Math.round(min() + (max() - min()) * value);
  }

  @Override
  protected double convertToRange(Integer value) {
    return (double) (value - min()) / (max() - min());
  }
}
