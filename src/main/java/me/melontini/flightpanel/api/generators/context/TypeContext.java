package me.melontini.flightpanel.api.generators.context;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public record TypeContext(Class<?> raw, Type type, AnnotatedType annotated) {

  public static TypeContext of(Type token, AnnotatedType annotatedType) {
    var lazy = TypeToken.get(token);
    return new TypeContext(lazy.getRawType(), lazy.getType(), annotatedType);
  }
}
