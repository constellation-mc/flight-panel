package dev.zenfyr.flightpanel.impl.elements;

import dev.zenfyr.flightpanel.api.builders.elements.BooleanToggleBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractValuedElement;
import dev.zenfyr.flightpanel.api.util.SquareData;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class BooleanToggleElement extends AbstractValuedElement<Boolean, BooleanToggleElement> {

  private final Button widget;

  public BooleanToggleElement(BooleanToggleBuilder builder) {
    super(builder);
    this.widget =
        new Button(
            0,
            0,
            20,
            20,
            value()
                ? Component.literal("✔").withStyle(ChatFormatting.GREEN)
                : Component.literal("❌").withStyle(ChatFormatting.RED),
            button -> value(!value()),
            Supplier::get) {
          @Override
          protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            this.isHovered = this.isHovered
                && BooleanToggleElement.this.proxy().isPointWithinListBounds(mouseX, mouseY);
            super.renderWidget(context, mouseX, mouseY, delta);
          }
        };

    this.listenToChange((b, nb) -> this.widget.setMessage(
        nb
            ? Component.literal("✔").withStyle(ChatFormatting.GREEN)
            : Component.literal("❌").withStyle(ChatFormatting.RED)));
  }

  @Override
  protected void resetToDefault(Boolean def) {
    value(def);
  }

  @Override
  public void rebuildPositions(SquareData self, SquareData parent) {
    super.rebuildPositions(self, parent);

    this.widget.setX(self.endX() - (resetButton.visible ? 42 : 21));
    this.widget.setY(self.y() + 1);
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    context.drawString(client.font, displayName(mouseX, mouseY), pos.x(), pos.y() + 7, -1);
    this.widget.render(context, mouseX, mouseY, delta);
  }

  @Override
  public void tick() {
    super.tick();
  }

  @Override
  public List<? extends GuiEventListener> children() {
    return List.of(widget, resetButton);
  }
}
