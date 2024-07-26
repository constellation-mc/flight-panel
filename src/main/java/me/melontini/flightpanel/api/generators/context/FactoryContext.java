package me.melontini.flightpanel.api.generators.context;

import lombok.*;
import lombok.experimental.Accessors;

@Builder
@With
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FactoryContext {
  TypeContext types;
  HierarchyAccessor accessor;
}
