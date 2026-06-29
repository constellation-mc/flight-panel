package me.melontini.flightpanel.impl.widgets.tab;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TabManager {

  private Consumer<Component> onTabSelect;

  @Nullable private Component currentTab;

  public TabManager(Consumer<Component> onTabSelect) {
    this.onTabSelect = onTabSelect;
  }

  public void setCurrentTab(Component tab, boolean clickSound) {
    if (!Objects.equals(this.currentTab, tab)) {
      this.currentTab = tab;
      this.onTabSelect.accept(tab);

      if (clickSound) {
        Minecraft.getInstance()
            .getSoundManager()
            .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
    }
  }

  @Nullable public Component getCurrentTab() {
    return this.currentTab;
  }
}
