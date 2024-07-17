package me.melontini.flightpanel.api.elements;

import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.flightpanel.api.builders.elements.ValuedElementBuilder;
import me.melontini.flightpanel.api.util.SquareData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractTextBoxElement<T, S extends AbstractTextBoxElement<T, S>> extends AbstractValuedElement<T, S> {

    private final TextFieldWidget inputField;
    @Nullable private Text inputError = null;

    public AbstractTextBoxElement(ValuedElementBuilder<T, S, ?> builder) {
        super(builder);
        this.inputField = new TextFieldWidget(client.textRenderer, 0, 0, 88 - 4, 18, Text.empty()) {
            @Override
            public void write(String text) {
                super.write(sanitizeString(text));
            }
        };
        this.inputField.setMaxLength(Integer.MAX_VALUE);
        this.inputField.setText(convertToString(value()));

        AtomicReference<T> str = new AtomicReference<>();
        this.inputField.setTextPredicate(s -> {
            if (Objects.isNull(s)) return false;
            if (Objects.equals(this.inputField.getText(), s)) return true;

            var r = convertFromString(s);
            if (r.error().isPresent()) {
                setInputError(r.error().get());
                return true;
            }
            setInputError(null);
            str.set(r.value().orElseThrow(IllegalStateException::new));
            return true;
        });
        this.inputField.setChangedListener(s -> {
            if (str.get() == null) return;
            value(str.getAndSet(null));
        });
    }

    @Override
    protected void resetToDefault(T def) {
        this.inputField.setText(convertToString(def));
    }

    protected abstract String convertToString(T obj);
    protected abstract Result<T, Text> convertFromString(String s);
    protected abstract String sanitizeString(String s);

    @Override
    public void rebuildPositions(SquareData self, SquareData parent) {
        super.rebuildPositions(self, parent);

        this.inputField.setX(self.endX() - (resetButton.visible ? 107 : 86));
        this.inputField.setY(self.y() + 2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(client.textRenderer, displayName(mouseX, mouseY), pos.x(), pos.y() + 7, -1);
        this.inputField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        this.inputField.tick();
    }

    protected void setInputError(@Nullable Text inputError) {
        this.inputError = inputError;
    }

    @Override
    public @Nullable Text getElementError() {
        return inputError;
    }

    @Override
    public boolean modified() {
        return inputError != null || super.modified();
    }

    @Override
    public List<? extends Element> children() {
        return List.of(this.inputField, this.resetButton);
    }
}
