package me.melontini.flightpanel.impl.generators;

import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.builders.elements.StringTextBoxBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.GuiProvider;
import me.melontini.flightpanel.api.generators.GuiProviderFactory;
import me.melontini.flightpanel.api.generators.GuiRegistry;
import me.melontini.flightpanel.api.generators.context.FactoryContext;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import me.melontini.flightpanel.api.util.Result;
import me.melontini.flightpanel.impl.elements.StringTextBoxElement;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StringProviderFactory implements GuiProviderFactory {

    @Override
    public @NotNull <T, A extends AbstractConfigElement<T, A>, SELF extends BaseElementBuilder<T, A, SELF>> Result<GuiProvider<T, A, SELF>, ? extends RuntimeException> createGuiProvider(GuiRegistry registry, FactoryContext context) {
        if (context.types().raw() != String.class) return Result.success(null);
        return Result.success((GuiProvider<T, A, SELF>) new StringProvider());
    }

    public record StringProvider() implements GuiProvider<String, StringTextBoxElement, StringTextBoxBuilder> {
        @Override
        public @NotNull StringTextBoxBuilder provideGui(String obj, Supplier<String> def, ProviderContext context) {
            return StringTextBoxBuilder.create(context.i18nOrEmpty(), value(obj, def).orElse("")).defaultValue(def);
        }
    }
}
