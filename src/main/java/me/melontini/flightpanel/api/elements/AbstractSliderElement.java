package me.melontini.flightpanel.api.elements;

import java.util.List;
import me.melontini.flightpanel.api.builders.elements.ValuedElementBuilder;
import me.melontini.flightpanel.api.util.SquareData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractSliderElement<T, S extends AbstractSliderElement<T, S>>
    extends AbstractValuedElement<T, S> {

  private final CustomSlider widget;

  public AbstractSliderElement(ValuedElementBuilder<T, S, ?> builder) {
    super(builder);
    this.widget = new CustomSlider(0, 0, 86, 20, Text.empty(), 0);
  }

  protected final void applyDefaults() {
    this.widget.setValue(convertToRange(value()));
  }

  protected abstract Text getMessage(T value);

  protected abstract T convertFromRange(double value);

  protected abstract double convertToRange(T value);

  @Override
  public void rebuildPositions(SquareData self, SquareData parent) {
    super.rebuildPositions(self, parent);

    this.widget.setX(self.endX() - (resetButton.visible ? 108 : 87));
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
  protected void resetToDefault(T def) {
    this.widget.setValue(convertToRange(def));
  }

  @Override
  public List<? extends Element> children() {
    return List.of(widget, resetButton);
  }

  public class CustomSlider extends SliderWidget {

    public CustomSlider(int x, int y, int width, int height, Text text, double value) {
      super(x, y, width, height, text, value);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
      this.hovered = this.hovered
          && AbstractSliderElement.this.proxy().isPointWithinListBounds(mouseX, mouseY);
      super.renderButton(context, mouseX, mouseY, delta);
    }

    @Override
    protected void updateMessage() {
      this.setMessage(AbstractSliderElement.this.getMessage(AbstractSliderElement.this.value()));
    }

    @Override
    protected void applyValue() {
      AbstractSliderElement.this.value(AbstractSliderElement.this.convertFromRange(this.value));
    }

    public void setValue(double val) {
      double d = this.value;
      this.value = MathHelper.clamp(val, 0.0, 1.0);
      if (d != this.value) {
        this.applyValue();
      }

      this.updateMessage();
    }
  }
}
