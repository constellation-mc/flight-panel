package me.melontini.flightpanel.api.generators;

import lombok.With;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@With
public record HierarchyAccessor(Class<?> topClass, Field field, AnnotatedType type) {

    public static HierarchyAccessor ofTopLevel(Class<?> topClass) {
        return new HierarchyAccessor(topClass, null, null);
    }

    public <T extends Annotation> T fromTopClass(Class<T> type) {
        return topClass().getAnnotation(type);
    }

    public <T extends Annotation> T fromField(Class<T> type) {
        return field().getAnnotation(type);
    }

    public <T extends Annotation> T fromType(Class<T> type) {
        return type().getAnnotation(type);
    }

    public <T extends Annotation> T fromBottomToTop(Class<T> type) {
        return selectFromList(List.of(this::fromType, this::fromField, this::fromTopClass), type);
    }

    public <T extends Annotation> T fromTopToBottom(Class<T> type) {
        return selectFromList(List.of(this::fromTopClass, this::fromField, this::fromType), type);
    }

    public <T extends Annotation> T selectFromList(List<Function<Class<T>, T>> list, Class<T> type) {
        return list.stream().map(f -> f.apply(type)).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
