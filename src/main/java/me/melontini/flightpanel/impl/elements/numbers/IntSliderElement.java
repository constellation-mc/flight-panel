package me.melontini.flightpanel.impl.elements.numbers;

import me.melontini.flightpanel.api.builders.elements.numbers.IntSliderBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberSliderElement;
import net.minecraft.text.Text;

public class IntSliderElement extends AbstractNumberSliderElement<Integer, IntSliderElement> {

  public IntSliderElement(IntSliderBuilder builder) {
    super(builder);
    this.applyDefaults();
  }

  @Override
  protected Text getMessage(Integer value) {
    return Text.literal(Integer.toString(value));
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
