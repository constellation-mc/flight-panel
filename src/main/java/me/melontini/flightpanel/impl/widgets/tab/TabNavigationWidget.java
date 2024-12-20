package me.melontini.flightpanel.impl.widgets.tab;

import com.google.common.collect.ImmutableList;
import java.util.*;
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

@Environment(EnvType.CLIENT)
public class TabNavigationWidget extends AbstractParentElement
    implements Drawable, Element, Selectable, TabButtonWidget.MousePosChecker {

  private static final ThreadLocal<Boolean> MOUSE_LOCK = ThreadLocal.withInitial(() -> false);

  private static final Text USAGE_NARRATION_TEXT =
      Text.translatable("narration.tab_navigation.usage");
  private final GridWidget grid;
  private final TabManager tabManager;
  private final ImmutableList<Text> tabs;
  private final ImmutableList<TabButtonWidget> tabButtons;

  private int tabNavWidth;
  private int tabAreaWidth;

  private int scrollPos = 0;
  private int maxScrollPos = 0;

  TabNavigationWidget(int width, TabManager tabManager, Iterable<Text> tabs) {
    this.setWidth(width);
    this.tabManager = tabManager;
    this.tabs = ImmutableList.copyOf(tabs);
    this.grid = new GridWidget(0, 0);
    this.grid.getMainPositioner().alignHorizontalCenter();
    ImmutableList.Builder<TabButtonWidget> builder = ImmutableList.builder();
    int i = 0;

    for (Text tab : tabs) {
      builder.add(this.grid.add(new TabButtonWidget(tabManager, tab, this, 0, 24), 0, i++));
    }

    this.tabButtons = builder.build();
  }

  public static Builder builder(TabManager tabManager, int width) {
    return new Builder(tabManager, width);
  }

  public void setWidth(int width) {
    this.tabNavWidth = width;
    this.tabAreaWidth = Math.min(400, this.tabNavWidth) - 14;
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
      if (MOUSE_LOCK.get()) return;

      int scroll = (tabButtonWidget.getX() + (tabButtonWidget.getWidth() / 2)) - this.grid.getX();

      if (scroll < this.tabAreaWidth) scroll = 0;
      else if (scroll > this.maxScrollPos) scroll = this.maxScrollPos;
      else scroll -= this.tabAreaWidth / 2;

      if (this.scrollPos != scroll) {
        this.scrollPos = scroll;
        this.rebuildPositions();
      }
    }
  }

  @Override
  public Optional<Element> hoveredElement(double mouseX, double mouseY) {
    if (!this.isTabAreaHovered(mouseX, mouseY)) return Optional.empty();
    return super.hoveredElement(mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (!this.isTabAreaHovered(mouseX, mouseY)) return false;
    try {
      MOUSE_LOCK.set(Boolean.TRUE);
      return super.mouseClicked(mouseX, mouseY, button);
    } finally {
      MOUSE_LOCK.remove();
    }
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (!this.isTabAreaHovered(mouseX, mouseY)) return false;

    int scrollPos = MathHelper.clamp(this.scrollPos + (int) (-amount * 10), 0, this.maxScrollPos);
    if (this.scrollPos != scrollPos) {
      this.scrollPos = scrollPos;
      this.rebuildPositions();
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, amount);
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
    return this.tabButtons.stream()
        .map(ClickableWidget::getType)
        .max(Comparator.naturalOrder())
        .orElse(SelectionType.NONE);
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    Optional<TabButtonWidget> optional = this.tabButtons.stream()
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
        builder.put(
            NarrationPart.POSITION,
            Text.translatable("narrator.position.tab", i + 1, this.tabs.size()));
      }
    }
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    if (this.tabButtons.size() > 1) {
      context.enableScissor(7, 0, this.tabAreaWidth + 7, 24);
      for (TabButtonWidget tabButtonWidget : this.tabButtons) {
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
    this.scrollPos = 0;
    var txr = MinecraftClient.getInstance().textRenderer;

    int sw = -tabAreaWidth;
    for (TabButtonWidget tabButtonWidget : this.tabButtons) {
      tabButtonWidget.setWidth(Math.max(txr.getWidth(tabButtonWidget.getTab()) + 6, 24));
      sw += tabButtonWidget.getWidth();
    }
    this.maxScrollPos = Math.max(0, sw);

    this.grid.refreshPositions();
    this.rebuildPositions();
    this.grid.setY(0);
  }

  public void rebuildPositions() {
    this.grid.setX(MathHelper.roundUpToMultiple((this.tabNavWidth - this.tabAreaWidth) / 2, 2)
        - this.scrollPos);
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
          return Math.floorDiv(j, this.tabs.size());
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

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return mouseX >= 0 && mouseX <= tabNavWidth && mouseY >= 0 && mouseY <= 24;
  }

  @Override
  public boolean isTabAreaHovered(double x, double y) {
    return x >= 7 && x <= (tabAreaWidth + 7) && y >= 0 && y <= 24;
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
