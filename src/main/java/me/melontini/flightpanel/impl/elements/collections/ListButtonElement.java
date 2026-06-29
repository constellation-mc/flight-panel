package me.melontini.flightpanel.impl.elements.collections;

import java.util.List;
import java.util.function.Function;
import me.melontini.flightpanel.api.builders.elements.collections.ListButtonBuilder;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.SquareData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class ListButtonElement<T> extends AbstractValuedElement<T, ListButtonElement<T>> {

  private final List<T> values;
  private final Function<T, Component> textifier;
  private final Button widget;

  public ListButtonElement(ListButtonBuilder<T> builder) {
    super(builder);
    this.values = builder.dataOrThrow(builder.valuesType());
    this.textifier = builder.dataOrThrow(builder.textifierType());

    this.widget = Button.builder(this.textifier.apply(this.value()), button -> {
          int index = this.values.indexOf(value());
          index = index >= this.values.size() - 1 ? 0 : index + 1;
          this.value(this.values.get(index));
        })
        .size(86, 20)
        .build();

    this.listenToChange(
        (oldValue, newValue) -> this.widget.setMessage(this.textifier.apply(newValue)));
  }

  @Override
  public void rebuildPositions(SquareData self, SquareData parent) {
    super.rebuildPositions(self, parent);

    this.widget.setX(self.endX() - (resetButton.visible ? 108 : 87));
    this.widget.setY(self.y() + 1);
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    context.drawString(client.font, displayName(mouseX, mouseY), pos.x(), pos.y() + 7, -1);
    this.widget.render(context, mouseX, mouseY, delta);
  }

  @Override
  protected void resetToDefault(T def) {
    this.value(def);
  }

  @Override
  public List<? extends GuiEventListener> children() {
    return List.of(widget, resetButton);
  }
}
