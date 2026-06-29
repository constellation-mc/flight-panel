package me.melontini.flightpanel.impl.widgets.tab;

import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class TabButtonWidget extends AbstractWidget {

  private static final ResourceLocation TEXTURE =
      ResourceLocation.tryParse("textures/gui/tab_button.png");
  private final TabManager tabManager;

  @Getter
  private final Component tab;

  private final MousePosChecker posChecker;

  public TabButtonWidget(
      TabManager tabManager, Component tab, MousePosChecker isHovered, int width, int height) {
    super(0, 0, width, height, tab);
    this.tabManager = tabManager;
    this.tab = tab;
    this.posChecker = isHovered;
  }

  @Override
  public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
    this.isHovered = this.posChecker.isTabAreaHovered(mouseX, mouseY) && this.isHovered;

    context.blitNineSliced(
        TEXTURE,
        this.getX(),
        this.getY(),
        this.width,
        this.height,
        2,
        2,
        2,
        0,
        130,
        24,
        0,
        this.getTextureV());
    Font textRenderer = Minecraft.getInstance().font;
    int i = this.active ? -1 : -6250336;
    this.drawMessage(context, textRenderer, i);
    if (this.isCurrentTab()) {
      this.drawCurrentTabLine(context, textRenderer, i);
    }
  }

  public void drawMessage(GuiGraphics context, Font textRenderer, int color) {
    int i = this.getX() + 1;
    int j = this.getY() + (this.isCurrentTab() ? 0 : 3);
    int k = this.getX() + this.getWidth() - 1;
    int l = this.getY() + this.getHeight();
    renderScrollingString(context, textRenderer, this.getMessage(), i, j, k, l, color);
  }

  private void drawCurrentTabLine(GuiGraphics context, Font textRenderer, int color) {
    int i = Math.min(textRenderer.width(this.getMessage()), this.getWidth() - 4);
    int j = this.getX() + (this.getWidth() - i) / 2;
    int k = this.getY() + this.getHeight() - 2;
    context.fill(j, k, j + i, k + 1, color);
  }

  protected int getTextureV() {
    int i = 2;
    if (this.isCurrentTab() && this.isHoveredOrFocused()) {
      i = 1;
    } else if (this.isCurrentTab()) {
      i = 0;
    } else if (this.isHoveredOrFocused()) {
      i = 3;
    }

    return i * 24;
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput output) {
    output.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.tab", this.tab));
  }

  @Override
  public void playDownSound(SoundManager soundManager) {}

  public boolean isCurrentTab() {
    return this.tabManager.getCurrentTab() == this.tab;
  }

  public interface MousePosChecker {
    boolean isTabAreaHovered(double x, double y);
  }
}
