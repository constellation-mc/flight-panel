package me.melontini.flightpanel.api.generators.context;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import lombok.*;
import lombok.experimental.Accessors;

@Builder
@With
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TypeContext {
  Class<?> raw;
  Type type;
  AnnotatedType annotated;

  public static TypeContext ofField(@NonNull Field field) {
    return of(field.getGenericType(), field.getAnnotatedType());
  }

  public static TypeContext of(@NonNull Type token, @NonNull AnnotatedType annotatedType) {
    var lazy = TypeToken.get(token);
    return new TypeContext(lazy.getRawType(), lazy.getType(), annotatedType);
  }
}
