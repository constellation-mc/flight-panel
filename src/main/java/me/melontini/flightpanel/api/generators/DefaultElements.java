package me.melontini.flightpanel.api.generators;

import me.melontini.flightpanel.impl.generators.*;

public class DefaultElements {

    public static class Factories {
        public static final GuiProviderFactory PRIMITIVE = new PrimitiveProviderFactory();
        public static final GuiProviderFactory STRING = new StringProviderFactory();
        public static final GuiProviderFactory LIST = new ListProviderFactory();
        public static final GuiProviderFactory ENUM = new EnumProviderFactory();
        public static final GuiProviderFactory COLLAPSIBLE_OBJECT = new CollapsibleObjectProviderFactory();
    }

    public static class Transformers {
        public static final GuiFieldTransformer REQUIRES_RESTART = (builder, field, context) -> {
            if (!field.isAnnotationPresent(Transformations.RequiresRestart.class)) return;
            builder.requireRestart();
        };
    }
}
