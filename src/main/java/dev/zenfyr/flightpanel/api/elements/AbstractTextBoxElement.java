package dev.zenfyr.flightpanel.api.elements;

import dev.zenfyr.flightpanel.api.builders.elements.ValuedElementBuilder;
import dev.zenfyr.flightpanel.api.util.SquareData;
import dev.zenfyr.pulsar.api.util.Result;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTextBoxElement<T, S extends AbstractTextBoxElement<T, S>>
    extends AbstractValuedElement<T, S> {

  private final EditBox inputField;

  @Nullable private Component inputError = null;

  public AbstractTextBoxElement(ValuedElementBuilder<T, S, ?> builder) {
    super(builder);
    this.inputField = new EditBox(client.font, 0, 0, 88 - 4, 18, Component.empty()) {
      @Override
      public void insertText(String text) {
        super.insertText(sanitizeString(text));
      }
    };
    this.inputField.setMaxLength(Integer.MAX_VALUE);
    this.inputField.setValue(convertToString(value()));

    AtomicReference<T> str = new AtomicReference<>();
    this.inputField.setFilter(s -> {
      if (Objects.isNull(s)) return false;
      if (Objects.equals(this.inputField.getValue(), s)) return true;

      var r = convertFromString(s);
      if (r.error().isPresent()) {
        setInputError(r.error().get());
        return true;
      }
      setInputError(null);
      str.set(r.value().orElseThrow(IllegalStateException::new));
      return true;
    });
    this.inputField.setResponder(s -> {
      if (str.get() == null) return;
      value(str.getAndSet(null));
    });
  }

  @Override
  protected void resetToDefault(T def) {
    this.inputField.setValue(convertToString(def));
  }

  protected abstract String convertToString(T obj);

  protected abstract Result<T, Component> convertFromString(String s);

  protected abstract String sanitizeString(String s);

  @Override
  public void rebuildPositions(SquareData self, SquareData parent) {
    super.rebuildPositions(self, parent);

    this.inputField.setX(self.endX() - (resetButton.visible ? 107 : 86));
    this.inputField.setY(self.y() + 2);
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);

    context.drawString(client.font, displayName(mouseX, mouseY), pos.x(), pos.y() + 7, -1);
    this.inputField.render(context, mouseX, mouseY, delta);
  }

  @Override
  public void tick() {
    super.tick();
    this.inputField.tick();
  }

  protected void setInputError(@Nullable Component inputError) {
    this.inputError = inputError;
  }

  @Override
  public @Nullable Component getElementError() {
    return inputError;
  }

  @Override
  public boolean modified() {
    return inputError != null || super.modified();
  }

  @Override
  public List<? extends GuiEventListener> children() {
    return List.of(this.inputField, this.resetButton);
  }
}
