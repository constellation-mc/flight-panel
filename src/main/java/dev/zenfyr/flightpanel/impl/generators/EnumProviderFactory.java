package dev.zenfyr.flightpanel.impl.generators;

import dev.zenfyr.flightpanel.api.builders.elements.BaseElementBuilder;
import dev.zenfyr.flightpanel.api.builders.elements.collections.ListButtonBuilder;
import dev.zenfyr.flightpanel.api.builders.elements.collections.ListSliderBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;
import dev.zenfyr.flightpanel.api.generators.GuiProvider;
import dev.zenfyr.flightpanel.api.generators.GuiProviderFactory;
import dev.zenfyr.flightpanel.api.generators.GuiRegistry;
import dev.zenfyr.flightpanel.api.generators.Transformations;
import dev.zenfyr.flightpanel.api.generators.context.FactoryContext;
import dev.zenfyr.flightpanel.api.generators.context.ProviderContext;
import dev.zenfyr.flightpanel.impl.elements.collections.ListButtonElement;
import dev.zenfyr.flightpanel.impl.elements.collections.ListSliderElement;
import dev.zenfyr.pulsar.api.util.Result;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnumProviderFactory implements GuiProviderFactory {

  @Override
  public @NotNull <T, A extends AbstractConfigElement<T, A>, S extends BaseElementBuilder<T, A, S>>
      Result<GuiProvider<T, A, S>, ? extends RuntimeException> createGuiProvider(
          GuiRegistry registry, FactoryContext context) {
    if (!context.types().raw().isEnum()) return Result.ok(null);
    List<Enum<?>> values =
        Arrays.asList(((Class<Enum<?>>) context.types().raw()).getEnumConstants());

    Transformations.Slider slider = context.accessor().fromType(Transformations.Slider.class);
    if (slider != null) {
      return Result.ok((GuiProvider<T, A, S>) new EnumSliderProvider<>(slider, values));
    }
    return Result.ok((GuiProvider<T, A, S>) new EnumButtonProvider<>(values));
  }

  public static <T extends Enum<T>> Function<T, Component> nameProvider(
      String i18n, Class<?> type) {
    return t -> {
      String remainingKey = i18n.contains(".option")
          ? i18n.substring(0, i18n.indexOf(".option") + ".option".length())
          : i18n;
      String classKey = String.format("%s.%s.%s", remainingKey, type.getSimpleName(), t.name());
      return I18n.exists(classKey) ? Component.translatable(classKey) : Component.literal(t.name());
    };
  }

  public record EnumButtonProvider<T extends Enum<T>>(List<Enum<?>> values)
      implements GuiProvider<T, ListButtonElement<T>, ListButtonBuilder<T>> {
    @Override
    public @NotNull ListButtonBuilder<T> provideGui(
        @Nullable T obj,
        @Nullable Supplier<@NotNull T> def,
        GuiRegistry registry,
        ProviderContext context) {
      return ListButtonBuilder.create(
              context.i18nOrEmpty(),
              value(obj, def).orElse((T) values.get(0)),
              (List<T>) values,
              nameProvider(context.i18n(), values.get(0).getDeclaringClass()))
          .defaultValue(def);
    }
  }

  public record EnumSliderProvider<T extends Enum<T>>(
      Transformations.Slider slider, List<Enum<?>> values)
      implements GuiProvider<T, ListSliderElement<T>, ListSliderBuilder<T>> {
    @Override
    public @NotNull ListSliderBuilder<T> provideGui(
        @Nullable T obj,
        @Nullable Supplier<@NotNull T> def,
        GuiRegistry registry,
        ProviderContext context) {
      return ListSliderBuilder.create(
              context.i18nOrEmpty(),
              value(obj, def).orElse((T) values.get(0)),
              (List<T>) values,
              nameProvider(context.i18n(), values.get(0).getDeclaringClass()))
          .defaultValue(def);
    }
  }
}
