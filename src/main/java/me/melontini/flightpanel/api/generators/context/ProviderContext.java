package me.melontini.flightpanel.api.generators.context;

import lombok.*;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;

@Builder
@With
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProviderContext {
  String i18n;
  boolean generic;

  public Component i18nOrEmpty() {
    return (i18n.isBlank() || generic) ? Component.empty() : Component.translatable(i18n);
  }
}
