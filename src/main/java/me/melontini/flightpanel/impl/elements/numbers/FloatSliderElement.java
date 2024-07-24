package me.melontini.flightpanel.impl.elements.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import me.melontini.flightpanel.api.builders.elements.numbers.DoubleSliderBuilder;
import me.melontini.flightpanel.api.builders.elements.numbers.FloatSliderBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberSliderElement;
import net.minecraft.text.Text;

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
  protected Text getMessage(Float value) {
    return Text.literal(Float.toString(value));
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
