package me.melontini.flightpanel.api.builders.elements;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Supplier;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.util.DataType;
import net.minecraft.text.Text;

public abstract class BaseElementBuilder<
    T, E extends AbstractConfigElement<T, E>, S extends BaseElementBuilder<T, E, S>> {

  public static DataType<Text> ELEMENT_NAME = DataType.of();
  public static DataType<Boolean> REQUIRES_RESTART = DataType.of();
  public static DataType<List<Text>> DESCRIPTION = DataType.of();

  private final IdentityHashMap<DataType<?>, Object> data = new IdentityHashMap<>();

  protected BaseElementBuilder(Text elementName) {
    this.data(ELEMENT_NAME, elementName);
  }

  public S elementName(Text text) {
    return data(ELEMENT_NAME, text);
  }

  public S requireRestart(boolean value) {
    return data(REQUIRES_RESTART, value);
  }

  public S requireRestart() {
    return requireRestart(true);
  }

  public S description(Text text) {
    return data(DESCRIPTION, Collections.singletonList(text));
  }

  public S description(List<Text> texts) {
    return data(DESCRIPTION, texts);
  }

  public final S self() {
    return (S) this;
  }

  public final <O> S data(DataType<O> type, O value) {
    this.data.put(type, value);
    return self();
  }

  public final <O> O data(DataType<O> type) {
    return (O) this.data.get(type);
  }

  public final <O> O dataOrThrow(DataType<O> type) {
    O value = data(type);
    if (value == null) throw new IllegalStateException();
    return value;
  }

  public final <O> O dataOrElse(DataType<O> type, O def) {
    O value = data(type);
    if (value == null) return def;
    return value;
  }

  public final <O> O dataOrElseGet(DataType<O> type, Supplier<O> def) {
    O value = data(type);
    if (value == null) return def.get();
    return value;
  }

  public abstract E build();
}
