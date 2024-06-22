package me.melontini.flightpanel.impl.widgets.tab;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TabManager {

	private Consumer<Text> onTabSelect;

	@Nullable
	private Text currentTab;

	public TabManager(Consumer<Text> onTabSelect) {
		this.onTabSelect = onTabSelect;
	}

	public void setCurrentTab(Text tab, boolean clickSound) {
		if (!Objects.equals(this.currentTab, tab)) {
			this.currentTab = tab;
			this.onTabSelect.accept(tab);

			if (clickSound) {
				MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
		}
	}

	@Nullable
	public Text getCurrentTab() {
		return this.currentTab;
	}
}
