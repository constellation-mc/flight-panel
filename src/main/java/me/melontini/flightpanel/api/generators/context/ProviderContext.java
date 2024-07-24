package me.melontini.flightpanel.api.generators.context;

import lombok.With;
import me.melontini.flightpanel.api.generators.GuiRegistry;
import net.minecraft.text.Text;

@With
public record ProviderContext(String i18n, boolean generic, GuiRegistry registry) {

  public Text i18nOrEmpty() {
    return (i18n.isBlank() || generic) ? Text.empty() : Text.translatable(i18n);
  }
}
