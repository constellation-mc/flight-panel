package me.melontini.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Floats;
import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import me.melontini.flightpanel.api.builders.elements.numbers.FloatTextBoxBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberTextBoxElement;
import net.minecraft.text.Text;

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
  protected Result<Float, Text> convertToNumber(String s) {
    var num = Floats.tryParse(s);
    return num == null
        ? Result.error(Text.translatable("service.flight-panel.error.number.invalid_float"))
        : Result.ok(num);
  }
}
