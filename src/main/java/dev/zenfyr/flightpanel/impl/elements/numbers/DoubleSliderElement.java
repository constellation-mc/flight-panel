package dev.zenfyr.flightpanel.impl.elements.numbers;

import dev.zenfyr.flightpanel.api.builders.elements.numbers.DoubleSliderBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberSliderElement;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.network.chat.Component;

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
  protected Component getMessage(Double value) {
    return Component.literal(Double.toString(value));
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
