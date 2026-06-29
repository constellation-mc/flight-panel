package dev.zenfyr.flightpanel.api.generators;

import dev.zenfyr.flightpanel.api.builders.elements.BaseElementBuilder;
import dev.zenfyr.flightpanel.api.generators.context.ProviderContext;
import java.lang.reflect.Field;

public interface GuiFieldTransformer {

  void transform(BaseElementBuilder<?, ?, ?> builder, Field field, ProviderContext context);
}
