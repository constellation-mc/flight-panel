package me.melontini.flightpanel.api.generators;

import lombok.NonNull;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.context.FactoryContext;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import me.melontini.flightpanel.api.util.Result;
import me.melontini.flightpanel.impl.generators.CollapsibleObjectProviderFactory;
import me.melontini.flightpanel.impl.generators.ListProviderFactory;
import me.melontini.flightpanel.impl.generators.PrimitiveProviderFactory;
import me.melontini.flightpanel.impl.generators.StringProviderFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

public class GuiRegistry implements GuiProviderFactory, GuiFieldTransformer {

    public static GuiRegistry empty() {
        return new GuiRegistry();
    }

    public static GuiRegistry withDefaults() {
        GuiRegistry def = GuiRegistry.empty();
        GuiRegistry.applyDefaultProviders(def);
        GuiRegistry.applyDefaultFieldTransformers(def);
        return def;
    }

    public static void applyDefaultProviders(GuiRegistry registry) {
        registry.registerProviderFactory(new PrimitiveProviderFactory());
        registry.registerProviderFactory(new StringProviderFactory());
        registry.registerProviderFactory(new ListProviderFactory());
        registry.registerProviderFactory(new CollapsibleObjectProviderFactory());
    }

    public static void applyDefaultFieldTransformers(GuiRegistry registry) {
        registry.registerFieldTransformer((builder, field, context) -> {
            if (!field.isAnnotationPresent(Transformations.RequiresRestart.class)) return;
            builder.requireRestart();
        });
    }

    private final Set<GuiProviderFactory> factories = new LinkedHashSet<>();
    private final Set<GuiFieldTransformer> fieldTransformers = new LinkedHashSet<>();

    private GuiRegistry() {}

    public void registerProviderFactory(@NonNull GuiProviderFactory factory) {
        this.factories.add(factory);
    }

    public void registerFieldTransformer(@NonNull GuiFieldTransformer transformer) {
        this.fieldTransformers.add(transformer);
    }

    @Override
    public <T, A extends AbstractConfigElement<T, A>, SELF extends BaseElementBuilder<T, A, SELF>> @NotNull Result<GuiProvider<T, A, SELF>, ? extends RuntimeException> createGuiProvider(GuiRegistry registry, FactoryContext context) {
        if (context.accessor().fromField(Transformations.Excluded.class) != null) return Result.success(null);

        for (GuiProviderFactory factory : this.factories) {
            Result<GuiProvider<T, A, SELF>, ? extends RuntimeException> r = factory.createGuiProvider(registry, context);
            if (r.value().isPresent() || r.error().isPresent()) return r;
        }

        return Result.error(new RuntimeException("No factory for type %s".formatted(context.types().type())));
    }

    @Override
    public void transform(BaseElementBuilder<?, ?, ?> builder, Field field, ProviderContext context) {
        for (GuiFieldTransformer t : this.fieldTransformers) {
            t.transform(builder, field, context);
        }
    }
}
