package dev.zenfyr.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Longs;
import dev.zenfyr.flightpanel.api.builders.elements.numbers.LongTextBoxBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractNumberTextBoxElement;
import dev.zenfyr.pulsar.api.util.Result;
import dev.zenfyr.pulsar.api.util.tuple.Tuple;
import net.minecraft.network.chat.Component;

public class LongTextBoxElement extends AbstractNumberTextBoxElement<Long, LongTextBoxElement> {

  public LongTextBoxElement(LongTextBoxBuilder builder) {
    super(builder);
  }

  @Override
  protected Tuple<Long, Long> defaultRange() {
    return Tuple.of(-Long.MAX_VALUE, Long.MAX_VALUE);
  }

  @Override
  protected boolean validChar(char c) {
    return false;
  }

  @Override
  protected String convertToString(Long obj) {
    return Long.toString(obj);
  }

  @Override
  protected Result<Long, Component> convertToNumber(String s) {
    var num = Longs.tryParse(s);
    return num == null
        ? Result.error(Component.translatable("service.flight-panel.error.number.invalid_long"))
        : Result.ok(num);
  }
}
