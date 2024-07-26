package me.melontini.flightpanel.api.generators.context;

import lombok.*;
import lombok.experimental.Accessors;
import net.minecraft.text.Text;

@Builder
@With
@Value
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProviderContext {
  String i18n;
  boolean generic;

  public Text i18nOrEmpty() {
    return (i18n.isBlank() || generic) ? Text.empty() : Text.translatable(i18n);
  }
}
