package dev.zenfyr.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Doubles;
import dev.zenfyr.flightpanel.api.builders.elements.numbers.DoubleTextBoxBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberTextBoxElement;
import dev.zenfyr.pulsar.api.util.Result;
import dev.zenfyr.pulsar.api.util.tuple.Tuple;
import net.minecraft.network.chat.Component;

public class DoubleTextBoxElement
    extends AbstractNumberTextBoxElement<Double, DoubleTextBoxElement> {

  public DoubleTextBoxElement(DoubleTextBoxBuilder builder) {
    super(builder);
  }

  @Override
  protected Tuple<Double, Double> defaultRange() {
    return Tuple.of(-Double.MAX_VALUE, Double.MAX_VALUE);
  }

  @Override
  protected boolean validChar(char c) {
    return c == '.' || c == 'E' || c == '-';
  }

  @Override
  protected String convertToString(Double obj) {
    return Double.toString(obj);
  }

  @Override
  protected Result<Double, Component> convertToNumber(String s) {
    var num = Doubles.tryParse(s);
    return num == null
        ? Result.error(Component.translatable("service.flight-panel.error.number.invalid_double"))
        : Result.ok(num);
  }
}
