package dev.zenfyr.flightpanel.impl.generators;

import dev.zenfyr.flightpanel.api.builders.elements.BaseElementBuilder;
import dev.zenfyr.flightpanel.api.builders.elements.collections.NestedListBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;
import dev.zenfyr.flightpanel.api.elements.AbstractValuedElement;
import dev.zenfyr.flightpanel.api.generators.GuiProvider;
import dev.zenfyr.flightpanel.api.generators.GuiProviderFactory;
import dev.zenfyr.flightpanel.api.generators.GuiRegistry;
import dev.zenfyr.flightpanel.api.generators.context.FactoryContext;
import dev.zenfyr.flightpanel.api.generators.context.ProviderContext;
import dev.zenfyr.flightpanel.api.generators.context.TypeContext;
import dev.zenfyr.flightpanel.impl.elements.collections.NestedListElement;
import dev.zenfyr.pulsar.api.util.Result;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ListProviderFactory implements GuiProviderFactory {

  @Override
  public @NotNull <T, A extends AbstractConfigElement<T, A>, S extends BaseElementBuilder<T, A, S>>
      Result<GuiProvider<T, A, S>, ? extends RuntimeException> createGuiProvider(
          GuiRegistry registry, FactoryContext context) {
    if (context.types().raw() != List.class
        || !(context.types().type() instanceof ParameterizedType params)) return Result.ok(null);

    Type object = params.getActualTypeArguments()[0];
    var layer = ((AnnotatedParameterizedType) context.types().annotated())
        .getAnnotatedActualTypeArguments()[0];
    Result<GuiProvider<T, A, S>, ? extends RuntimeException> elementAdapter =
        registry.createGuiProvider(
            registry,
            FactoryContext.builder()
                .types(TypeContext.of(object, layer))
                .accessor(context.accessor().withType(layer))
                .build());

    if (elementAdapter.error().isPresent())
      return Result.error(elementAdapter.error().get());
    if (elementAdapter.value().isEmpty()) return Result.ok(null);

    return Result.ok((GuiProvider<T, A, S>)
        new ListProvider<>((GuiProvider<T, ? extends AbstractValuedElement<T, ?>, ?>)
            elementAdapter.value().get()));
  }

  public record ListProvider<T>(
      GuiProvider<T, ? extends AbstractValuedElement<T, ?>, ?> elementAdapter)
      implements GuiProvider<List<T>, NestedListElement<T>, NestedListBuilder<T>> {

    @Override
    public @NotNull NestedListBuilder<T> provideGui(
        List<T> obj, Supplier<List<T>> def, GuiRegistry registry, ProviderContext context) {
      var entryCtx = context.withGeneric(true);
      return NestedListBuilder.create(
              context.i18nOrEmpty(),
              value(obj, def).orElse(Collections.emptyList()),
              (t, e) -> elementAdapter.provideGui(t, null, registry, entryCtx).build(),
              () -> null)
          .defaultValue(def);
    }
  }
}
