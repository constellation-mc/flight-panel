package me.melontini.flightpanel.api.builders.elements;

import me.melontini.flightpanel.impl.elements.BooleanToggleElement;
import net.minecraft.network.chat.Component;

public class BooleanToggleBuilder
    extends ValuedElementBuilder<Boolean, BooleanToggleElement, BooleanToggleBuilder> {

  public static BooleanToggleBuilder create(Component elementName, boolean value) {
    return new BooleanToggleBuilder(elementName, value);
  }

  protected BooleanToggleBuilder(Component elementName, boolean value) {
    super(elementName, value);
  }

  @Override
  public BooleanToggleElement build() {
    return new BooleanToggleElement(this);
  }
}
