package me.melontini.flightpanel.impl.widgets.tab;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class TabNavigationWidget extends AbstractParentElement implements Drawable, Element, Selectable {

	private static final Text USAGE_NARRATION_TEXT = Text.translatable("narration.tab_navigation.usage");
	private final GridWidget grid;
	private int tabNavWidth;
	private final TabManager tabManager;
	private final ImmutableList<Text> tabs;
	private final ImmutableList<TabButtonWidget> tabButtons;

	TabNavigationWidget(int width, TabManager tabManager, Iterable<Text> tabs) {
		this.tabNavWidth = width;
		this.tabManager = tabManager;
		this.tabs = ImmutableList.copyOf(tabs);
		this.grid = new GridWidget(0, 0);
		this.grid.getMainPositioner().alignHorizontalCenter();
		ImmutableList.Builder<TabButtonWidget> builder = ImmutableList.builder();
		int i = 0;

		for(Text tab : tabs) {
			builder.add(this.grid.add(new TabButtonWidget(tabManager, tab, 0, 24), 0, i++));
		}

		this.tabButtons = builder.build();
	}

	public static Builder builder(TabManager tabManager, int width) {
		return new Builder(tabManager, width);
	}

	public void setWidth(int width) {
		this.tabNavWidth = width;
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if (this.getFocused() != null) {
			this.getFocused().setFocused(focused);
		}
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		super.setFocused(focused);
		if (focused instanceof TabButtonWidget tabButtonWidget) {
			this.tabManager.setCurrentTab(tabButtonWidget.getTab(), true);
		}
	}

	@Nullable @Override
	public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
		if (!this.isFocused()) {
			TabButtonWidget tabButtonWidget = this.getCurrentTabButton();
			if (tabButtonWidget != null) {
				return GuiNavigationPath.of(this, GuiNavigationPath.of(tabButtonWidget));
			}
		}

		return navigation instanceof GuiNavigation.Tab ? null : super.getNavigationPath(navigation);
	}

	@Override
	public List<? extends Element> children() {
		return this.tabButtons;
	}

	@Override
	public SelectionType getType() {
		return this.tabButtons.stream().map(ClickableWidget::getType).max(Comparator.naturalOrder()).orElse(SelectionType.NONE);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		Optional<TabButtonWidget> optional = this.tabButtons
			.stream()
			.filter(ClickableWidget::isHovered)
			.findFirst()
			.or(() -> Optional.ofNullable(this.getCurrentTabButton()));
		optional.ifPresent(button -> {
			this.appendNarrations(builder.nextMessage(), button);
			button.appendNarrations(builder);
		});
		if (this.isFocused()) {
			builder.put(NarrationPart.USAGE, USAGE_NARRATION_TEXT);
		}
	}

	protected void appendNarrations(NarrationMessageBuilder builder, TabButtonWidget button) {
		if (this.tabs.size() > 1) {
			int i = this.tabButtons.indexOf(button);
			if (i != -1) {
				builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.tab", i + 1, this.tabs.size()));
			}
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.tabButtons.size() > 1) {
			context.enableScissor(7, 0, Math.min(400, this.tabNavWidth) - 7, 24);
			for(TabButtonWidget tabButtonWidget : this.tabButtons) {
				tabButtonWidget.render(context, mouseX, mouseY, delta);
			}
			context.disableScissor();
		}
	}

	@Override
	public ScreenRect getNavigationFocus() {
		return this.grid.getNavigationFocus();
	}

	public void init() {
		var txr = MinecraftClient.getInstance().textRenderer;
		int i = Math.min(400, this.tabNavWidth) - 14;

		for(TabButtonWidget tabButtonWidget : this.tabButtons) {
			tabButtonWidget.setWidth(Math.max(txr.getWidth(tabButtonWidget.getTab()) + 6, 24));
		}

		this.grid.refreshPositions();
		this.grid.setX(MathHelper.roundUpToMultiple((this.tabNavWidth - i) / 2, 2));
		this.grid.setY(0);
	}

	public void selectTab(int index, boolean clickSound) {
		if (this.isFocused()) {
			this.setFocused(this.tabButtons.get(index));
		} else {
			this.tabManager.setCurrentTab(this.tabs.get(index), clickSound);
		}
	}

	public boolean trySwitchTabsWithKey(int keyCode) {
		if (Screen.hasControlDown()) {
			int i = this.getTabForKey(keyCode);
			if (i != -1) {
				this.selectTab(MathHelper.clamp(i, 0, this.tabs.size() - 1), true);
				return true;
			}
		}

		return false;
	}

	private int getTabForKey(int keyCode) {
		if (keyCode >= 49 && keyCode <= 57) {
			return keyCode - 49;
		} else {
			if (keyCode == 258) {
				int i = this.getCurrentTabIndex();
				if (i != -1) {
					int j = Screen.hasShiftDown() ? i - 1 : i + 1;
					return Math.floorMod(j, this.tabs.size());
				}
			}

			return -1;
		}
	}

	private int getCurrentTabIndex() {
		Text tab = this.tabManager.getCurrentTab();
		int i = this.tabs.indexOf(tab);
		return i != -1 ? i : -1;
	}

	@Nullable private TabButtonWidget getCurrentTabButton() {
		int i = this.getCurrentTabIndex();
		return i != -1 ? this.tabButtons.get(i) : null;
	}

	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final int width;
		private final TabManager tabManager;
		private final List<Text> tabs = new ArrayList<>();

		Builder(TabManager tabManager, int width) {
			this.tabManager = tabManager;
			this.width = width;
		}

		public Builder tabs(Text... tabs) {
			Collections.addAll(this.tabs, tabs);
			return this;
		}

		public TabNavigationWidget build() {
			return new TabNavigationWidget(this.width, this.tabManager, this.tabs);
		}
	}
}
