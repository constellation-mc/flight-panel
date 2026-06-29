package dev.zenfyr.flightpanel.impl.elements.numbers;

import dev.zenfyr.flightpanel.api.builders.elements.numbers.DoubleSliderBuilder;
import dev.zenfyr.flightpanel.api.builders.elements.numbers.FloatSliderBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberSliderElement;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.network.chat.Component;

public class FloatSliderElement extends AbstractNumberSliderElement<Float, FloatSliderElement> {

  private final int places;
  private final RoundingMode mode;

  public FloatSliderElement(FloatSliderBuilder builder) {
    super(builder);
    this.places = builder.dataOrElse(DoubleSliderBuilder.PLACES, 2);
    this.mode = builder.dataOrElse(DoubleSliderBuilder.ROUNDING_MODE, RoundingMode.HALF_UP);
    this.applyDefaults();
  }

  @Override
  protected Component getMessage(Float value) {
    return Component.literal(Float.toString(value));
  }

  @Override
  protected Float convertFromRange(double value) {
    BigDecimal val = BigDecimal.valueOf(min() + (max() - min()) * value);
    return val.setScale(places, mode).floatValue();
  }

  @Override
  protected double convertToRange(Float value) {
    return (value - min()) / (max() - min());
  }
}
