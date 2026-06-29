package dev.zenfyr.flightpanel.impl.widgets;

import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;
import dev.zenfyr.flightpanel.impl.util.TextUtil;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

@Accessors(fluent = true)
public class OptionInfoWidget implements Renderable, GuiEventListener, NarratableEntry {

  public static final Component NOTHING_SELECTED = Component.translatable(
          "service.flight-panel.widget.option_info.nothing_selected")
      .withStyle(ChatFormatting.GRAY);

  private final Minecraft client = Minecraft.getInstance();

  @Getter
  @Setter
  private int x, y, width, height;

  @Getter
  private AbstractConfigElement<?, ?> display;

  private List<FormattedCharSequence> optionTitle;
  private List<FormattedCharSequence> optionDescription;

  public OptionInfoWidget(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    int y = this.y + 8;
    context.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
    if (display == null) {
      context.drawString(client.font, NOTHING_SELECTED, x + 8, y, -1, true);
      context.disableScissor();
      return;
    }

    for (FormattedCharSequence text : this.optionTitle) {
      context.drawString(client.font, text, x + 8, y, -1, true);
      y += 9;
    }

    if (!optionDescription.isEmpty()) {
      y += 6;
      for (FormattedCharSequence text : this.optionDescription) {
        context.drawString(client.font, text, x + 8, y, -1, true);
        y += 11;
      }
    }
    context.disableScissor();
  }

  public void display(AbstractConfigElement<?, ?> display) {
    if (display == null || TextUtil.isBlank(display.elementName())) {
      this.display = null;
      this.optionTitle = null;
      this.optionDescription = null;
      return;
    }
    this.display = display;
    this.optionTitle = client.font.split(
        display.elementName().copy().withStyle(ChatFormatting.BOLD), width - 8 - 8);
    this.optionDescription = display.description().stream()
        .map(text -> client.font.split(text, width - 8 - 8))
        .flatMap(Collection::stream)
        .toList();
  }

  @Override
  public void setFocused(boolean focused) {}

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public void updateNarration(NarrationElementOutput output) {}
}
