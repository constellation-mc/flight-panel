package me.melontini.flightpanel.api.builders.elements;

import me.melontini.flightpanel.impl.elements.BooleanToggleElement;
import net.minecraft.text.Text;

public class BooleanToggleBuilder
    extends ValuedElementBuilder<Boolean, BooleanToggleElement, BooleanToggleBuilder> {

  public static BooleanToggleBuilder create(Text elementName, boolean value) {
    return new BooleanToggleBuilder(elementName, value);
  }

  protected BooleanToggleBuilder(Text elementName, boolean value) {
    super(elementName, value);
  }

  @Override
  public BooleanToggleElement build() {
    return new BooleanToggleElement(this);
  }
}
