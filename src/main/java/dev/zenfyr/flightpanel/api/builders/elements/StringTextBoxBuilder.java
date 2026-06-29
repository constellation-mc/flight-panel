package dev.zenfyr.flightpanel.api.builders.elements;

import dev.zenfyr.flightpanel.api.util.DataType;
import dev.zenfyr.flightpanel.impl.elements.StringTextBoxElement;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class StringTextBoxBuilder
    extends ValuedElementBuilder<String, StringTextBoxElement, StringTextBoxBuilder> {

  public static final DataType<Function<String, String>> SANITIZER = DataType.of();

  public static StringTextBoxBuilder create(Component elementName, String value) {
    return new StringTextBoxBuilder(elementName, value);
  }

  protected StringTextBoxBuilder(Component elementName, String value) {
    super(elementName, value);
  }

  public StringTextBoxBuilder sanitizer(Function<String, String> sanitizer) {
    return data(SANITIZER, sanitizer);
  }

  @Override
  public StringTextBoxElement build() {
    return new StringTextBoxElement(this);
  }
}
