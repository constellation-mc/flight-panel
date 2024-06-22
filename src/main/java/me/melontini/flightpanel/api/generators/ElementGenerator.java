package me.melontini.flightpanel.api.generators;

import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.builders.elements.CollapsibleObjectBuilder;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import me.melontini.flightpanel.impl.generators.CollapsibleObjectProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class ElementGenerator {

    public static <T> @Unmodifiable @NotNull Collection<BaseElementBuilder<?, ?, ?>> generateForObject(String i18n, @NonNull T obj, Supplier<@NotNull T> defSupplier, Optional<GuiRegistry> reg) {
        GuiRegistry registry = reg.orElseGet(GuiRegistry::withDefaults);
        if (i18n.endsWith(".")) i18n = i18n.substring(0, i18n.length() - 1);

        var entryContext = new ProviderContext(i18n, false, registry);
        var factory = CollapsibleObjectProviderFactory.forAnyObject(registry, (Class<T>) obj.getClass());
        return ImmutableList.copyOf(factory.provideGui(obj, defSupplier, entryContext).dataOrThrow(CollapsibleObjectBuilder.ELEMENTS));
    }
}
