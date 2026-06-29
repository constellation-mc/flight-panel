package dev.zenfyr.flightpanel.api.builders.elements.numbers;

import dev.zenfyr.flightpanel.api.util.DataType;
import dev.zenfyr.flightpanel.impl.elements.numbers.DoubleSliderElement;
import java.math.RoundingMode;
import net.minecraft.network.chat.Component;

public class DoubleSliderBuilder
    extends RangedNumberElementBuilder<Double, DoubleSliderElement, DoubleSliderBuilder> {

  public static final DataType<Integer> PLACES = DataType.of();
  public static final DataType<RoundingMode> ROUNDING_MODE = DataType.of();

  public static DoubleSliderBuilder create(
      Component elementName, double value, double min, double max) {
    return new DoubleSliderBuilder(elementName, value, min, max);
  }

  protected DoubleSliderBuilder(Component elementName, double value, double min, double max) {
    super(elementName, value);
    this.min(min);
    this.max(max);
  }

  public DoubleSliderBuilder places(int places) {
    return data(PLACES, places);
  }

  public DoubleSliderBuilder roundingMode(RoundingMode mode) {
    return data(ROUNDING_MODE, mode);
  }

  @Override
  public DoubleSliderElement build() {
    return new DoubleSliderElement(this);
  }
}
