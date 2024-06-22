package me.melontini.flightpanel.api.generators;

import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public interface GuiProvider<T, A extends AbstractConfigElement<T, A>, SELF extends BaseElementBuilder<T, A, SELF>> {

    @NotNull SELF provideGui(@Nullable T obj, @Nullable Supplier<@NotNull T> def, ProviderContext context);

    default Optional<T> value(T obj, Supplier<T> def) {
        if (obj != null) return Optional.of(obj);
        return def != null ? Optional.ofNullable(def.get()) : Optional.empty();
    }
}
