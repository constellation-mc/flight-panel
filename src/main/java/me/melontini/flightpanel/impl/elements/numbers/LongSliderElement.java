package me.melontini.flightpanel.impl.elements.numbers;

import me.melontini.flightpanel.api.builders.elements.numbers.LongSliderBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberSliderElement;
import net.minecraft.text.Text;

public class LongSliderElement extends AbstractNumberSliderElement<Long, LongSliderElement> {

  public LongSliderElement(LongSliderBuilder builder) {
    super(builder);
    this.applyDefaults();
  }

  @Override
  protected Text getMessage(Long value) {
    return Text.literal(Long.toString(value));
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
