package me.melontini.flightpanel.api.generators;

import lombok.NonNull;
import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.generators.context.FactoryContext;
import me.melontini.flightpanel.api.generators.context.ProviderContext;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

public class GuiRegistry implements GuiProviderFactory, GuiFieldTransformer {

    public static GuiRegistry create() {
        return new GuiRegistry();
    }

    public static GuiRegistry withDefaults() {
        GuiRegistry def = GuiRegistry.create();
        def.registerProviderFactory(DefaultElements.Factories.PRIMITIVE);
        def.registerProviderFactory(DefaultElements.Factories.STRING);
        def.registerProviderFactory(DefaultElements.Factories.LIST);
        def.registerProviderFactory(DefaultElements.Factories.ENUM);
        def.registerProviderFactory(DefaultElements.Factories.COLLAPSIBLE_OBJECT);

        def.registerFieldTransformer(DefaultElements.Transformers.REQUIRES_RESTART);
        return def;
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
        if (context.accessor().fromField(Transformations.Excluded.class) != null) return Result.ok(null);

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
