package me.melontini.flightpanel.impl.generators;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;
import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.builders.elements.CollapsibleObjectBuilder;
import me.melontini.flightpanel.api.builders.elements.ValuedElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.GuiProvider;
import me.melontini.flightpanel.api.generators.GuiProviderFactory;
import me.melontini.flightpanel.api.generators.GuiRegistry;
import me.melontini.flightpanel.api.generators.Transformations;
import me.melontini.flightpanel.api.generators.context.FactoryContext;
import me.melontini.flightpanel.api.generators.context.HierarchyAccessor;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import me.melontini.flightpanel.api.generators.context.TypeContext;
import me.melontini.flightpanel.impl.elements.CollapsibleObjectElement;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

public class CollapsibleObjectProviderFactory implements GuiProviderFactory {

  @Override
  public @NotNull <T, A extends AbstractConfigElement<T, A>, S extends BaseElementBuilder<T, A, S>>
      Result<GuiProvider<T, A, S>, ? extends RuntimeException> createGuiProvider(
          GuiRegistry registry, FactoryContext context) {
    var ann = context.accessor().fromType(Transformations.Collapsible.class);
    if (ann == null) return Result.ok(null);

    var gen = baseGenerator(registry, context.types().raw(), ann.collapsed(), ann.keyType());
    if (gen.error().isPresent()) return Result.error(gen.error().get());
    if (gen.value().isEmpty()) return Result.ok(null);

    return Result.ok((GuiProvider<T, A, S>) gen.value().get());
  }

  public static <O> CollapsibleObjectProvider<O> forAnyObject(GuiRegistry registry, Class<O> type) {
    var gen = baseGenerator(registry, type, false, Transformations.Collapsible.KeyType.DEFAULT);
    if (gen.error().isPresent()) throw gen.error().get();
    if (gen.value().isEmpty())
      throw new IllegalStateException("Failed to create new CollapsibleObjectProvider!");
    return gen.value().get();
  }

  public static <
          O, T, A extends AbstractConfigElement<T, A>, SELF extends BaseElementBuilder<T, A, SELF>>
      Result<CollapsibleObjectProvider<O>, ? extends RuntimeException> baseGenerator(
          GuiRegistry registry,
          Class<O> type,
          boolean collapsed,
          Transformations.Collapsible.KeyType keyType) {
    if (ClassUtils.isPrimitiveOrWrapper(type) || type.isRecord() || type.isEnum() || type.isArray())
      return Result.error(
          new RuntimeException("Cannot transform primitive, record, array and enum types! (%s)"
              .formatted(type.getName())));

    Map<Field, GuiProvider<?, ?, ?>> fields = new LinkedHashMap<>();
    HierarchyAccessor accessor = HierarchyAccessor.ofTopLevel(type);

    Set<Result<?, ? extends RuntimeException>> errors = new LinkedHashSet<>();

    for (Field field : type.getFields()) {
      Result<GuiProvider<T, A, SELF>, ? extends RuntimeException> adapter =
          registry.createGuiProvider(
              registry,
              FactoryContext.builder()
                  .types(TypeContext.ofField(field))
                  .accessor(accessor.withAnnotatedField(field))
                  .build());
      if (adapter.error().isPresent()) {
        errors.add(adapter);
        continue;
      }
      if (adapter.value().isEmpty()) continue;

      fields.put(field, adapter.value().get());
    }

    if (!errors.isEmpty()) {
      if (errors.size() == 1)
        return Result.error(errors.stream().findFirst().orElseThrow().error().orElseThrow());

      var exc = new IllegalStateException("Multiple Problems!");
      exc.setStackTrace(new StackTraceElement[0]);
      errors.stream()
          .map(result -> result.error())
          .map(Optional::orElseThrow)
          .peek(e -> e.setStackTrace(
              Arrays.stream(e.getStackTrace()).limit(4).toArray(StackTraceElement[]::new)))
          .forEach(exc::addSuppressed);
      return Result.error(exc);
    }

    return Result.ok(new CollapsibleObjectProvider<>(collapsed, keyType, type, fields));
  }

  public record CollapsibleObjectProvider<T>(
      boolean collapsed,
      Transformations.Collapsible.KeyType keyType,
      Class<?> raw,
      Map<Field, GuiProvider<?, ?, ?>> providers)
      implements GuiProvider<T, CollapsibleObjectElement<T>, CollapsibleObjectBuilder<T>> {

    @Override
    public @NotNull CollapsibleObjectBuilder<T> provideGui(
        T obj, Supplier<T> def, GuiRegistry registry, ProviderContext ctx) {
      if (def == null)
        def = () -> {
          try {
            return (T) raw.getConstructor().newInstance();
          } catch (InstantiationException
              | IllegalAccessException
              | InvocationTargetException
              | NoSuchMethodException e) {
            throw new RuntimeException("Collapsible object has no no-args constructor!", e);
          }
        };
      T value = obj == null ? def.get() : obj;
      T defValue = def.get();

      var context = keyType().isGeneric() ? ctx.withGeneric(true) : ctx;

      String remainingKey = context.i18n().contains(".option")
          ? context.i18n().substring(0, context.i18n().indexOf(".option") + ".option".length())
          : context.i18n();
      String classKey = String.format("%s.%s", remainingKey, raw.getSimpleName());

      var b = CollapsibleObjectBuilder.create(
              Text.translatable(!context.generic() ? context.i18n() : classKey), value)
          .collapsed(collapsed());
      var entryCtx = context.withGeneric(false);
      boolean genericFields = context.generic() || keyType().isGenericFields();

      providers.forEach((field, guiProvider) -> {
        String iI13n = (genericFields ? "%s.%s" : "%s.option.%s")
            .formatted(genericFields ? classKey : context.i18n(), field.getName());
        var currentCtx = entryCtx.withI18n(iI13n);
        var builder = providers
            .get(field)
            .provideGui(
                getField(field, value), () -> getField(field, defValue), registry, currentCtx);
        registry.transform(builder, field, currentCtx);
        if (builder instanceof ValuedElementBuilder<?, ?, ?> afb)
          afb.saveFunction(e -> setField(field, value, e));
        b.element(builder);
      });

      return b.defaultValue(def);
    }
  }

  private static <T> T getField(Field field, Object object) {
    try {
      return (T) field.get(object);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static void setField(Field field, Object object, Object value) {
    try {
      field.set(object, value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
