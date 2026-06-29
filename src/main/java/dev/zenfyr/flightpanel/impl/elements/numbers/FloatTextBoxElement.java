package dev.zenfyr.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Floats;
import dev.zenfyr.flightpanel.api.builders.elements.numbers.FloatTextBoxBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberTextBoxElement;
import dev.zenfyr.pulsar.api.util.Result;
import dev.zenfyr.pulsar.api.util.tuple.Tuple;
import net.minecraft.network.chat.Component;

public class FloatTextBoxElement extends AbstractNumberTextBoxElement<Float, FloatTextBoxElement> {

  public FloatTextBoxElement(FloatTextBoxBuilder builder) {
    super(builder);
  }

  @Override
  protected Tuple<Float, Float> defaultRange() {
    return Tuple.of(-Float.MAX_VALUE, Float.MAX_VALUE);
  }

  @Override
  protected boolean validChar(char c) {
    return c == '.' || c == 'E' || c == '-';
  }

  @Override
  protected String convertToString(Float obj) {
    return Float.toString(obj);
  }

  @Override
  protected Result<Float, Component> convertToNumber(String s) {
    var num = Floats.tryParse(s);
    return num == null
        ? Result.error(Component.translatable("service.flight-panel.error.number.invalid_float"))
        : Result.ok(num);
  }
}
