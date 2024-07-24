package me.melontini.flightpanel.api.generators.context;

import lombok.NonNull;
import lombok.With;

@With
public record FactoryContext(TypeContext types, HierarchyAccessor accessor) {

  public static FactoryContext of(@NonNull TypeContext types, @NonNull HierarchyAccessor accessor) {
    return new FactoryContext(types, accessor);
  }
}
