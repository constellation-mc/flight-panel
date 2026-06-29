package dev.zenfyr.flightpanel.api.generators;

import dev.zenfyr.flightpanel.api.builders.elements.BaseElementBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;
import dev.zenfyr.flightpanel.api.generators.context.FactoryContext;
import dev.zenfyr.pulsar.api.util.Result;
import org.jetbrains.annotations.NotNull;

public interface GuiProviderFactory {

  @NotNull <T, A extends AbstractConfigElement<T, A>, S extends BaseElementBuilder<T, A, S>>
      Result<GuiProvider<T, A, S>, ? extends RuntimeException> createGuiProvider(
          GuiRegistry registry, FactoryContext context);
}
