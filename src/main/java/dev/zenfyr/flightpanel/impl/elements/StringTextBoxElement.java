package dev.zenfyr.flightpanel.impl.elements;

import dev.zenfyr.flightpanel.api.builders.elements.StringTextBoxBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractTextBoxElement;
import dev.zenfyr.pulsar.api.util.Result;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class StringTextBoxElement extends AbstractTextBoxElement<String, StringTextBoxElement> {

  private final Function<String, String> sanitizer;

  public StringTextBoxElement(StringTextBoxBuilder builder) {
    super(builder);
    this.sanitizer = builder.dataOrElse(StringTextBoxBuilder.SANITIZER, Function.identity());
  }

  @Override
  protected String convertToString(String obj) {
    return obj;
  }

  @Override
  protected Result<String, Component> convertFromString(String s) {
    return Result.ok(s);
  }

  @Override
  protected String sanitizeString(String s) {
    return this.sanitizer.apply(s);
  }
}
