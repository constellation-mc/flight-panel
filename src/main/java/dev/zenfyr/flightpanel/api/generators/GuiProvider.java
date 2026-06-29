package dev.zenfyr.flightpanel.api.generators;

import dev.zenfyr.flightpanel.api.builders.elements.BaseElementBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;
import dev.zenfyr.flightpanel.api.generators.context.ProviderContext;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuiProvider<
    T, A extends AbstractConfigElement<T, A>, SELF extends BaseElementBuilder<T, A, SELF>> {

  @NotNull SELF provideGui(
      @Nullable T obj,
      @Nullable Supplier<@NotNull T> def,
      GuiRegistry registry,
      ProviderContext context);

  default Optional<T> value(T obj, Supplier<T> def) {
    if (obj != null) return Optional.of(obj);
    return def != null ? Optional.ofNullable(def.get()) : Optional.empty();
  }
}
