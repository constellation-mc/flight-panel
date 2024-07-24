package me.melontini.flightpanel.impl.elements.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import me.melontini.flightpanel.api.builders.elements.numbers.DoubleSliderBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberSliderElement;
import net.minecraft.text.Text;

public class DoubleSliderElement extends AbstractNumberSliderElement<Double, DoubleSliderElement> {

  private final int places;
  private final RoundingMode mode;

  public DoubleSliderElement(DoubleSliderBuilder builder) {
    super(builder);
    this.places = builder.dataOrElse(DoubleSliderBuilder.PLACES, 2);
    this.mode = builder.dataOrElse(DoubleSliderBuilder.ROUNDING_MODE, RoundingMode.HALF_UP);
    this.applyDefaults();
  }

  @Override
  protected Text getMessage(Double value) {
    return Text.literal(Double.toString(value));
  }

  @Override
  protected Double convertFromRange(double value) {
    BigDecimal val = BigDecimal.valueOf(min() + (max() - min()) * value);
    return val.setScale(places, mode).doubleValue();
  }

  @Override
  protected double convertToRange(Double value) {
    return (value - min()) / (max() - min());
  }
}
