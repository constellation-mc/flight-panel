package me.melontini.flightpanel.impl.widgets.tab;

import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TabButtonWidget extends ClickableWidget {

  private static final Identifier TEXTURE = new Identifier("textures/gui/tab_button.png");
  private final TabManager tabManager;

  @Getter
  private final Text tab;

  private final MousePosChecker isHovered;

  public TabButtonWidget(
      TabManager tabManager, Text tab, MousePosChecker isHovered, int width, int height) {
    super(0, 0, width, height, tab);
    this.tabManager = tabManager;
    this.tab = tab;
    this.isHovered = isHovered;
  }

  @Override
  public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
    this.hovered = this.isHovered.isTabAreaHovered(mouseX, mouseY) && this.hovered;

    context.drawNineSlicedTexture(
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
    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    int i = this.active ? -1 : -6250336;
    this.drawMessage(context, textRenderer, i);
    if (this.isCurrentTab()) {
      this.drawCurrentTabLine(context, textRenderer, i);
    }
  }

  public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
    int i = this.getX() + 1;
    int j = this.getY() + (this.isCurrentTab() ? 0 : 3);
    int k = this.getX() + this.getWidth() - 1;
    int l = this.getY() + this.getHeight();
    drawScrollableText(context, textRenderer, this.getMessage(), i, j, k, l, color);
  }

  private void drawCurrentTabLine(DrawContext context, TextRenderer textRenderer, int color) {
    int i = Math.min(textRenderer.getWidth(this.getMessage()), this.getWidth() - 4);
    int j = this.getX() + (this.getWidth() - i) / 2;
    int k = this.getY() + this.getHeight() - 2;
    context.fill(j, k, j + i, k + 1, color);
  }

  protected int getTextureV() {
    int i = 2;
    if (this.isCurrentTab() && this.isSelected()) {
      i = 1;
    } else if (this.isCurrentTab()) {
      i = 0;
    } else if (this.isSelected()) {
      i = 3;
    }

    return i * 24;
  }

  @Override
  protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE, Text.translatable("gui.narrate.tab", this.tab));
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
