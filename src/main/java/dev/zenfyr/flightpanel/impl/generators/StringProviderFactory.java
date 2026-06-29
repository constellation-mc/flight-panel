package dev.zenfyr.flightpanel.impl.generators;

import dev.zenfyr.flightpanel.api.builders.elements.BaseElementBuilder;
import dev.zenfyr.flightpanel.api.builders.elements.StringTextBoxBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;
import dev.zenfyr.flightpanel.api.generators.GuiProvider;
import dev.zenfyr.flightpanel.api.generators.GuiProviderFactory;
import dev.zenfyr.flightpanel.api.generators.GuiRegistry;
import dev.zenfyr.flightpanel.api.generators.context.FactoryContext;
import dev.zenfyr.flightpanel.api.generators.context.ProviderContext;
import dev.zenfyr.flightpanel.impl.elements.StringTextBoxElement;
import dev.zenfyr.pulsar.api.util.Result;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class StringProviderFactory implements GuiProviderFactory {

  @Override
  public @NotNull <
          T, A extends AbstractConfigElement<T, A>, SELF extends BaseElementBuilder<T, A, SELF>>
      Result<GuiProvider<T, A, SELF>, ? extends RuntimeException> createGuiProvider(
          GuiRegistry registry, FactoryContext context) {
    if (context.types().raw() != String.class) return Result.ok(null);
    return Result.ok((GuiProvider<T, A, SELF>) new StringProvider());
  }

  public record StringProvider()
      implements GuiProvider<String, StringTextBoxElement, StringTextBoxBuilder> {
    @Override
    public @NotNull StringTextBoxBuilder provideGui(
        String obj, Supplier<String> def, GuiRegistry registry, ProviderContext context) {
      return StringTextBoxBuilder.create(context.i18nOrEmpty(), value(obj, def).orElse(""))
          .defaultValue(def);
    }
  }
}
