package me.melontini.flightpanel.api.generators;

import java.lang.reflect.Field;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.generators.context.ProviderContext;

public interface GuiFieldTransformer {

  void transform(BaseElementBuilder<?, ?, ?> builder, Field field, ProviderContext context);
}
