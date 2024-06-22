package me.melontini.flightpanel.api.generators;

import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.generators.context.ProviderContext;

import java.lang.reflect.Field;

public interface GuiFieldTransformer {

    void transform(BaseElementBuilder<?, ?, ?> builder, Field field, ProviderContext context);
}
