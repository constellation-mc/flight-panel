package me.melontini.flightpanel.api.builders;

import lombok.NonNull;
import me.melontini.flightpanel.impl.ConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigScreenBuilder {

    public static ConfigScreenBuilder create() {
        return new ConfigScreenBuilder();
    }

    private Text title = Text.empty();
    private Screen parent = null;
    private Runnable saveFunction = () -> {};
    private final Map<Text, CategoryBuilder> categories = new LinkedHashMap<>();

    public ConfigScreenBuilder title(@NonNull Text title) {
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

    public CategoryBuilder category(Text title) {
        return categories.computeIfAbsent(title, CategoryBuilder::new);
    }

    public ConfigScreen build() {
        return new ConfigScreen(title,
                parent == null ? MinecraftClient.getInstance().currentScreen : parent,
                categories.values(), saveFunction);
    }
}
