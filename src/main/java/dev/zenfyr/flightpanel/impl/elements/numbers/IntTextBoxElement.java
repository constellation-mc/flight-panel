package dev.zenfyr.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Ints;
import dev.zenfyr.flightpanel.api.builders.elements.numbers.IntTextBoxBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberTextBoxElement;
import dev.zenfyr.pulsar.api.util.Result;
import dev.zenfyr.pulsar.api.util.tuple.Tuple;
import net.minecraft.network.chat.Component;

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
  protected Result<Integer, Component> convertToNumber(String s) {
    var num = Ints.tryParse(s);
    return num == null
        ? Result.error(Component.translatable("service.flight-panel.error.number.invalid_int"))
        : Result.ok(num);
  }
}
