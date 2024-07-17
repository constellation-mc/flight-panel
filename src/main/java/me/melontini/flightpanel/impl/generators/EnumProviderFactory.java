package me.melontini.flightpanel.impl.generators;

import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.builders.elements.collections.ListButtonBuilder;
import me.melontini.flightpanel.api.builders.elements.collections.ListSliderBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.GuiProvider;
import me.melontini.flightpanel.api.generators.GuiProviderFactory;
import me.melontini.flightpanel.api.generators.GuiRegistry;
import me.melontini.flightpanel.api.generators.Transformations;
import me.melontini.flightpanel.api.generators.context.FactoryContext;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import me.melontini.flightpanel.impl.elements.collections.ListButtonElement;
import me.melontini.flightpanel.impl.elements.collections.ListSliderElement;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnumProviderFactory implements GuiProviderFactory {

    @Override
    public @NotNull <T, A extends AbstractConfigElement<T, A>, S extends BaseElementBuilder<T, A, S>> Result<GuiProvider<T, A, S>, ? extends RuntimeException> createGuiProvider(GuiRegistry registry, FactoryContext context) {
        if (!context.types().raw().isEnum()) return Result.ok(null);
        List<Enum<?>> values = Arrays.asList(((Class<Enum<?>>)context.types().raw()).getEnumConstants());

        Transformations.Slider slider = context.accessor().fromType(Transformations.Slider.class);
        if (slider != null) {
            return Result.ok((GuiProvider<T, A, S>) new EnumSliderProvider<>(slider, values));
        }
        return Result.ok((GuiProvider<T, A, S>) new EnumButtonProvider<>(values));
    }

    public static <T extends Enum<T>> Function<T, Text> nameProvider(String i18n, Class<?> type) {
        return t -> {
            String remainingKey = i18n.contains(".option") ? i18n.substring(0, i18n.indexOf(".option") + ".option".length()) : i18n;
            String classKey = String.format("%s.%s.%s", remainingKey, type.getSimpleName(), t.name());
            return I18n.hasTranslation(classKey) ? Text.translatable(classKey) : Text.literal(t.name());
        };
    }

    public record EnumButtonProvider<T extends Enum<T>>(List<Enum<?>> values) implements GuiProvider<T, ListButtonElement<T>, ListButtonBuilder<T>> {
        @Override
        public @NotNull ListButtonBuilder<T> provideGui(@Nullable T obj, @Nullable Supplier<@NotNull T> def, ProviderContext context) {
            return ListButtonBuilder.create(context.i18nOrEmpty(), value(obj, def).orElse((T) values.get(0)), (List<T>) values, nameProvider(context.i18n(), values.get(0).getDeclaringClass()))
                    .defaultValue(def);
        }
    }

    public record EnumSliderProvider<T extends Enum<T>>(Transformations.Slider slider, List<Enum<?>> values) implements GuiProvider<T, ListSliderElement<T>, ListSliderBuilder<T>> {
        @Override
        public @NotNull ListSliderBuilder<T> provideGui(@Nullable T obj, @Nullable Supplier<@NotNull T> def, ProviderContext context) {
            return ListSliderBuilder.create(context.i18nOrEmpty(), value(obj, def).orElse((T) values.get(0)), (List<T>) values, nameProvider(context.i18n(), values.get(0).getDeclaringClass()))
                    .defaultValue(def);
        }
    }
}
