package me.melontini.flightpanel.api.builders.elements;

import java.util.List;
import me.melontini.flightpanel.api.util.DataType;
import me.melontini.flightpanel.impl.elements.CollapsibleObjectElement;
import net.minecraft.text.Text;
import org.apache.commons.compress.utils.Lists;

public class CollapsibleObjectBuilder<T>
    extends ValuedElementBuilder<T, CollapsibleObjectElement<T>, CollapsibleObjectBuilder<T>> {

  public static final DataType<List<BaseElementBuilder<?, ?, ?>>> ELEMENTS = DataType.of();
  public static final DataType<Boolean> COLLAPSED = DataType.of();

  public static <T> CollapsibleObjectBuilder<T> create(Text elementName, T value) {
    return new CollapsibleObjectBuilder<>(elementName, value);
  }

  protected CollapsibleObjectBuilder(Text elementName, T value) {
    super(elementName, value);
    this.data(ELEMENTS, Lists.newArrayList());
  }

  public CollapsibleObjectBuilder<T> collapsed(boolean value) {
    return this.data(COLLAPSED, value);
  }

  public CollapsibleObjectBuilder<T> collapsed() {
    return this.collapsed(true);
  }

  public CollapsibleObjectBuilder<T> expanded() {
    return this.collapsed(false);
  }

  public CollapsibleObjectBuilder<T> element(BaseElementBuilder<?, ?, ?> builder) {
    this.data(ELEMENTS).add(builder);
    return this;
  }

  @Override
  public CollapsibleObjectElement<T> build() {
    return new CollapsibleObjectElement<>(this);
  }
}
