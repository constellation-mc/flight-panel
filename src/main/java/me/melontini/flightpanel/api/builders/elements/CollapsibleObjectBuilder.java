package me.melontini.flightpanel.api.builders.elements;

import java.util.*;
import me.melontini.flightpanel.api.util.DataType;
import me.melontini.flightpanel.impl.elements.CollapsibleObjectElement;
import net.minecraft.text.Text;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

public class CollapsibleObjectBuilder<T>
    extends ValuedElementBuilder<T, CollapsibleObjectElement<T>, CollapsibleObjectBuilder<T>>
    implements List<BaseElementBuilder<?, ?, ?>> {

  public static final DataType<List<BaseElementBuilder<?, ?, ?>>> ELEMENTS = DataType.of();
  public static final DataType<Boolean> COLLAPSED = DataType.of();

  public static CollapsibleObjectBuilder<?> create(Text elementName) {
    return create(elementName, Optional.empty());
  }

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

  @Override
  public int size() {
    return this.data(ELEMENTS).size();
  }

  @Override
  public boolean isEmpty() {
    return this.data(ELEMENTS).isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return this.data(ELEMENTS).contains(o);
  }

  @NotNull @Override
  public Iterator<BaseElementBuilder<?, ?, ?>> iterator() {
    return this.data(ELEMENTS).iterator();
  }

  @NotNull @Override
  public Object[] toArray() {
    return this.data(ELEMENTS).toArray();
  }

  @NotNull @Override
  public <T> T[] toArray(@NotNull T[] a) {
    return this.data(ELEMENTS).toArray(a);
  }

  @Override
  public boolean add(BaseElementBuilder<?, ?, ?> baseElementBuilder) {
    return this.data(ELEMENTS).add(baseElementBuilder);
  }

  @Override
  public boolean remove(Object o) {
    return this.data(ELEMENTS).remove(o);
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> c) {
    return this.data(ELEMENTS).containsAll(c);
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends BaseElementBuilder<?, ?, ?>> c) {
    return this.data(ELEMENTS).addAll(c);
  }

  @Override
  public boolean addAll(int index, @NotNull Collection<? extends BaseElementBuilder<?, ?, ?>> c) {
    return this.data(ELEMENTS).addAll(index, c);
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    return this.data(ELEMENTS).removeAll(c);
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    return this.data(ELEMENTS).retainAll(c);
  }

  @Override
  public void clear() {
    this.data(ELEMENTS).clear();
  }

  @Override
  public BaseElementBuilder<?, ?, ?> get(int index) {
    return this.data(ELEMENTS).get(index);
  }

  @Override
  public BaseElementBuilder<?, ?, ?> set(int index, BaseElementBuilder<?, ?, ?> element) {
    return this.data(ELEMENTS).set(index, element);
  }

  @Override
  public void add(int index, BaseElementBuilder<?, ?, ?> element) {
    this.data(ELEMENTS).set(index, element);
  }

  @Override
  public BaseElementBuilder<?, ?, ?> remove(int index) {
    return this.data(ELEMENTS).remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return this.data(ELEMENTS).indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return this.data(ELEMENTS).lastIndexOf(o);
  }

  @NotNull @Override
  public ListIterator<BaseElementBuilder<?, ?, ?>> listIterator() {
    return this.data(ELEMENTS).listIterator();
  }

  @NotNull @Override
  public ListIterator<BaseElementBuilder<?, ?, ?>> listIterator(int index) {
    return this.data(ELEMENTS).listIterator(index);
  }

  @NotNull @Override
  public List<BaseElementBuilder<?, ?, ?>> subList(int fromIndex, int toIndex) {
    return this.data(ELEMENTS).subList(fromIndex, toIndex);
  }
}
