package me.melontini.flightpanel.impl.elements;

import java.util.List;
import java.util.function.Supplier;
import me.melontini.flightpanel.api.builders.elements.BooleanToggleBuilder;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.SquareData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BooleanToggleElement extends AbstractValuedElement<Boolean, BooleanToggleElement> {

  private final ButtonWidget widget;

  public BooleanToggleElement(BooleanToggleBuilder builder) {
    super(builder);
    this.widget =
        new ButtonWidget(
            0,
            0,
            20,
            20,
            value()
                ? Text.literal("✔").formatted(Formatting.GREEN)
                : Text.literal("❌").formatted(Formatting.RED),
            button -> value(!value()),
            Supplier::get) {
          @Override
          protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            this.hovered = this.hovered
                && BooleanToggleElement.this.proxy().isPointWithinListBounds(mouseX, mouseY);
            super.renderButton(context, mouseX, mouseY, delta);
          }
        };

    this.listenToChange((b, nb) -> this.widget.setMessage(
        nb
            ? Text.literal("✔").formatted(Formatting.GREEN)
            : Text.literal("❌").formatted(Formatting.RED)));
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
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    context.drawTextWithShadow(
        client.textRenderer, displayName(mouseX, mouseY), pos.x(), pos.y() + 7, -1);
    this.widget.render(context, mouseX, mouseY, delta);
  }

  @Override
  public void tick() {
    super.tick();
  }

  @Override
  public List<? extends Element> children() {
    return List.of(widget, resetButton);
  }
}
