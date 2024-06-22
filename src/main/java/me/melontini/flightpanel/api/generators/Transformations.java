package me.melontini.flightpanel.api.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

public final class Transformations {

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Range {
        double from() default 0;
        double to();
    }

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Slider {
        int places() default 2;
        RoundingMode mode() default RoundingMode.HALF_UP;
    }

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Collapsible {
        boolean collapsed() default true;
        KeyType keyType() default KeyType.DEFAULT;

        enum KeyType {
            DEFAULT,
            GENERIC,
            GENERIC_FIELDS;

            public boolean isDefault() {
                return this == DEFAULT;
            }

            public boolean isGeneric() {
                return this == GENERIC;
            }

            public boolean isGenericFields() {
                return this == GENERIC_FIELDS;
            }
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Excluded {
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequiresRestart {
    }
}
