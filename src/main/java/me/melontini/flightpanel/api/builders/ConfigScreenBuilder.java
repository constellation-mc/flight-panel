package me.melontini.flightpanel.api.builders;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;
import me.melontini.flightpanel.impl.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ConfigScreenBuilder {

  public static ConfigScreenBuilder create() {
    return new ConfigScreenBuilder();
  }

  private Component title = Component.empty();
  private Screen parent = null;
  private Runnable saveFunction = () -> {};
  private final Map<Component, CategoryBuilder> categories = new LinkedHashMap<>();

  public ConfigScreenBuilder title(@NonNull Component title) {
    this.title = title;
    return this;
  }

  public ConfigScreenBuilder parent(@Nullable Screen parent) {
    this.parent = parent;
    return this;
  }

  public ConfigScreenBuilder saveFunction(@NonNull Runnable saveFunction) {
    this.saveFunction = saveFunction;
    return this;
  }

  public CategoryBuilder category(Component title) {
    return categories.computeIfAbsent(title, CategoryBuilder::new);
  }

  public ConfigScreen build() {
    return new ConfigScreen(
        title, parent == null ? Minecraft.getInstance().screen : parent, categories, saveFunction);
  }
}
