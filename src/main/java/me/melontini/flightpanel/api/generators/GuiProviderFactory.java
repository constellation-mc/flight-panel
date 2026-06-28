package me.melontini.flightpanel.api.generators;

import dev.zenfyr.pulsar.api.util.Result;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.context.FactoryContext;
import org.jetbrains.annotations.NotNull;

public interface GuiProviderFactory {

  @NotNull <T, A extends AbstractConfigElement<T, A>, S extends BaseElementBuilder<T, A, S>>
      Result<GuiProvider<T, A, S>, ? extends RuntimeException> createGuiProvider(
          GuiRegistry registry, FactoryContext context);
}
