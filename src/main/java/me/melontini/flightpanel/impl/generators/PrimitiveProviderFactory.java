package me.melontini.flightpanel.impl.generators;

import static me.melontini.flightpanel.api.generators.Transformations.Range;
import static me.melontini.flightpanel.api.generators.Transformations.Slider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.builders.elements.BooleanToggleBuilder;
import me.melontini.flightpanel.api.builders.elements.numbers.*;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.GuiProvider;
import me.melontini.flightpanel.api.generators.GuiProviderFactory;
import me.melontini.flightpanel.api.generators.GuiRegistry;
import me.melontini.flightpanel.api.generators.context.FactoryContext;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import me.melontini.flightpanel.impl.elements.BooleanToggleElement;
import me.melontini.flightpanel.impl.elements.numbers.*;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

public class PrimitiveProviderFactory implements GuiProviderFactory {

  private static final Map<Class<?>, Function<Range, ? extends GuiProvider<?, ?, ?>>> factories =
      new HashMap<>();
  private static final Map<Class<?>, BiFunction<Slider, Range, ? extends GuiProvider<?, ?, ?>>>
      sliderFactories = new HashMap<>();

  static {
    factories.put(int.class, IntProvider::new);
    factories.put(long.class, LongProvider::new);
    factories.put(float.class, FloatProvider::new);
    factories.put(double.class, DoubleProvider::new);
    factories.put(boolean.class, BooleanProvider::new);

    sliderFactories.put(int.class, IntSliderProvider::new);
    sliderFactories.put(long.class, LongSliderProvider::new);
    sliderFactories.put(float.class, FloatSliderProvider::new);
    sliderFactories.put(double.class, DoubleSliderProvider::new);
  }

  @Override
  public @NotNull <T, A extends AbstractConfigElement<T, A>, S extends BaseElementBuilder<T, A, S>>
      Result<GuiProvider<T, A, S>, ? extends RuntimeException> createGuiProvider(
          GuiRegistry registry, FactoryContext context) {
    if (!ClassUtils.isPrimitiveOrWrapper(context.types().raw())) return Result.ok(null);
    var type = Objects.requireNonNullElse(
        ClassUtils.wrapperToPrimitive(context.types().raw()), context.types().raw());

    Range range = context.accessor().fromType(Range.class);
    Slider slider = context.accessor().fromType(Slider.class);

    if (slider != null) {
      BiFunction<Slider, Range, ? extends GuiProvider<?, ?, ?>> factory = sliderFactories.get(type);
      if (factory == null)
        return Result.error(new RuntimeException("No @Slider factory for this primitive %s!"
            .formatted(context.types().type())));
      if (range == null) return Result.error(new RuntimeException("@Slider requires @Range!"));

      return Result.ok((GuiProvider<T, A, S>) factory.apply(slider, range));
    }

    Function<Range, ? extends GuiProvider<?, ?, ?>> factory = factories.get(type);
    if (factory == null)
      return Result.error(new RuntimeException(
          "No factory for this primitive %s!".formatted(context.types().type())));

    return Result.ok((GuiProvider<T, A, S>) factory.apply(range));
  }

  public record IntSliderProvider(Slider slider, Range ranged)
      implements GuiProvider<Integer, IntSliderElement, IntSliderBuilder> {
    @Override
    public @NotNull IntSliderBuilder provideGui(
        Integer obj, Supplier<Integer> def, GuiRegistry registry, ProviderContext context) {
      return IntSliderBuilder.create(
              context.i18nOrEmpty(),
              value(obj, def).orElse((int) ranged.from()),
              (int) ranged.from(),
              (int) ranged.to())
          .defaultValue(def);
    }
  }

  public record LongSliderProvider(Slider slider, Range ranged)
      implements GuiProvider<Long, LongSliderElement, LongSliderBuilder> {
    @Override
    public @NotNull LongSliderBuilder provideGui(
        Long obj, Supplier<Long> def, GuiRegistry registry, ProviderContext context) {
      return LongSliderBuilder.create(
              context.i18nOrEmpty(),
              value(obj, def).orElse((long) ranged.from()),
              (long) ranged.from(),
              (long) ranged.to())
          .defaultValue(def);
    }
  }

  public record DoubleSliderProvider(Slider slider, Range ranged)
      implements GuiProvider<Double, DoubleSliderElement, DoubleSliderBuilder> {
    @Override
    public @NotNull DoubleSliderBuilder provideGui(
        Double obj, Supplier<Double> def, GuiRegistry registry, ProviderContext context) {
      return DoubleSliderBuilder.create(
              context.i18nOrEmpty(),
              value(obj, def).orElse(ranged.from()),
              ranged.from(),
              ranged.to())
          .roundingMode(slider.mode())
          .places(slider.places())
          .defaultValue(def);
    }
  }

  public record FloatSliderProvider(Slider slider, Range ranged)
      implements GuiProvider<Float, FloatSliderElement, FloatSliderBuilder> {
    @Override
    public @NotNull FloatSliderBuilder provideGui(
        Float obj, Supplier<Float> def, GuiRegistry registry, ProviderContext context) {
      return FloatSliderBuilder.create(
              context.i18nOrEmpty(),
              value(obj, def).orElse((float) ranged.from()),
              (float) ranged.from(),
              (float) ranged.to())
          .roundingMode(slider.mode())
          .places(slider.places())
          .defaultValue(def);
    }
  }

  public record IntProvider(Range ranged)
      implements GuiProvider<Integer, IntTextBoxElement, IntTextBoxBuilder> {
    @Override
    public @NotNull IntTextBoxBuilder provideGui(
        Integer obj, Supplier<Integer> def, GuiRegistry registry, ProviderContext context) {
      var b = IntTextBoxBuilder.create(
          context.i18nOrEmpty(), value(obj, def).orElse(ranged != null ? (int) ranged.from() : 0));
      if (ranged != null) b.min((int) ranged.from()).max((int) ranged.to());
      return b.defaultValue(def);
    }
  }

  public record LongProvider(Range ranged)
      implements GuiProvider<Long, LongTextBoxElement, LongTextBoxBuilder> {
    @Override
    public @NotNull LongTextBoxBuilder provideGui(
        Long obj, Supplier<Long> def, GuiRegistry registry, ProviderContext context) {
      var b = LongTextBoxBuilder.create(
          context.i18nOrEmpty(), value(obj, def).orElse(ranged != null ? (long) ranged.from() : 0));
      if (ranged != null) b.min((long) ranged.from()).max((long) ranged.to());
      return b.defaultValue(def);
    }
  }

  public record FloatProvider(Range ranged)
      implements GuiProvider<Float, FloatTextBoxElement, FloatTextBoxBuilder> {
    @Override
    public @NotNull FloatTextBoxBuilder provideGui(
        Float obj, Supplier<Float> def, GuiRegistry registry, ProviderContext context) {
      var b = FloatTextBoxBuilder.create(
          context.i18nOrEmpty(),
          value(obj, def).orElse(ranged != null ? (float) ranged.from() : 0));
      if (ranged != null) b.min((float) ranged.from()).max((float) ranged.to());
      return b.defaultValue(def);
    }
  }

  public record DoubleProvider(Range ranged)
      implements GuiProvider<Double, DoubleTextBoxElement, DoubleTextBoxBuilder> {
    @Override
    public @NotNull DoubleTextBoxBuilder provideGui(
        Double obj, Supplier<Double> def, GuiRegistry registry, ProviderContext context) {
      var b = DoubleTextBoxBuilder.create(
          context.i18nOrEmpty(), value(obj, def).orElse(ranged != null ? ranged.from() : 0));
      if (ranged != null) b.min(ranged.from()).max(ranged.to());
      return b.defaultValue(def);
    }
  }

  public record BooleanProvider(Range ranged)
      implements GuiProvider<Boolean, BooleanToggleElement, BooleanToggleBuilder> {
    @Override
    public @NotNull BooleanToggleBuilder provideGui(
        Boolean obj, Supplier<Boolean> def, GuiRegistry registry, ProviderContext context) {
      return BooleanToggleBuilder.create(context.i18nOrEmpty(), value(obj, def).orElse(false))
          .defaultValue(def);
    }
  }
}
