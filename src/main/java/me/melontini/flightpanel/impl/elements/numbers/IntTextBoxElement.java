package me.melontini.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Ints;
import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import me.melontini.flightpanel.api.builders.elements.numbers.IntTextBoxBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberTextBoxElement;
import net.minecraft.text.Text;

public class IntTextBoxElement extends AbstractNumberTextBoxElement<Integer, IntTextBoxElement> {

  public IntTextBoxElement(IntTextBoxBuilder builder) {
    super(builder);
  }

  @Override
  protected Tuple<Integer, Integer> defaultRange() {
    return Tuple.of(-Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  @Override
  protected boolean validChar(char c) {
    return false;
  }

  @Override
  protected String convertToString(Integer obj) {
    return Integer.toString(obj);
  }

  @Override
  protected Result<Integer, Text> convertToNumber(String s) {
    var num = Ints.tryParse(s);
    return num == null
        ? Result.error(Text.translatable("service.flight-panel.error.number.invalid_int"))
        : Result.ok(num);
  }
}
