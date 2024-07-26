package me.melontini.flightpanel.api.generators.context;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.*;
import lombok.experimental.Accessors;

@Builder
@With
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HierarchyAccessor {
  Class<?> topClass;

  @Builder.Default
  Field field = null;

  @Builder.Default
  AnnotatedType type = null;

  public static HierarchyAccessor ofTopLevel(@NonNull Class<?> topClass) {
    return new HierarchyAccessor(topClass, null, null);
  }

  public <T extends Annotation> T fromTopClass(Class<T> type) {
    if (topClass() == null) return null;
    return topClass().getAnnotation(type);
  }

  public <T extends Annotation> T fromField(Class<T> type) {
    if (field() == null) return null;
    return field().getAnnotation(type);
  }

  public <T extends Annotation> T fromType(Class<T> type) {
    if (type() == null) return null;
    return type().getAnnotation(type);
  }

  public <T extends Annotation> T fromBottomToTop(Class<T> type) {
    return selectFromList(List.of(this::fromType, this::fromField, this::fromTopClass), type);
  }

  public <T extends Annotation> T fromTopToBottom(Class<T> type) {
    return selectFromList(List.of(this::fromTopClass, this::fromField, this::fromType), type);
  }

  public <T extends Annotation> T selectFromList(List<Function<Class<T>, T>> list, Class<T> type) {
    return list.stream()
        .map(f -> f.apply(type))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  public HierarchyAccessor withAnnotatedField(Field field) {
    return field == this.field
        ? this
        : new HierarchyAccessor(this.topClass, field, field.getAnnotatedType());
  }
}
