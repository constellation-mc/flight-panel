package dev.zenfyr.flightpanel.impl.elements.numbers;

import dev.zenfyr.flightpanel.api.builders.elements.numbers.LongSliderBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberSliderElement;
import net.minecraft.network.chat.Component;

public class LongSliderElement extends AbstractNumberSliderElement<Long, LongSliderElement> {

  public LongSliderElement(LongSliderBuilder builder) {
    super(builder);
    this.applyDefaults();
  }

  @Override
  protected Component getMessage(Long value) {
    return Component.literal(Long.toString(value));
  }

  @Override
  protected Long convertFromRange(double value) {
    return Math.round(min() + (max() - min()) * value);
  }

  @Override
  protected double convertToRange(Long value) {
    return (double) (value - min()) / (max() - min());
  }
}
