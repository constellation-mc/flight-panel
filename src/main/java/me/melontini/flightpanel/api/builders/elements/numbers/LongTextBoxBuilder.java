package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.LongTextBoxElement;
import net.minecraft.text.Text;

public class LongTextBoxBuilder
    extends RangedNumberElementBuilder<Long, LongTextBoxElement, LongTextBoxBuilder> {

  public static LongTextBoxBuilder create(Text elementName, long value) {
    return new LongTextBoxBuilder(elementName, value);
  }

  protected LongTextBoxBuilder(Text elementName, long value) {
    super(elementName, value);
  }

  @Override
  public LongTextBoxElement build() {
    return new LongTextBoxElement(this);
  }
}
