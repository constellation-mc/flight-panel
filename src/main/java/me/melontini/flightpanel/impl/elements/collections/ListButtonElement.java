package me.melontini.flightpanel.impl.elements.collections;

import me.melontini.flightpanel.api.builders.elements.collections.ListButtonBuilder;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.SquareData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Function;

public class ListButtonElement<T> extends AbstractValuedElement<T, ListButtonElement<T>> {

    private final List<T> values;
    private final Function<T, Text> textifier;
    private final ButtonWidget widget;

    public ListButtonElement(ListButtonBuilder<T> builder) {
        super(builder);
        this.values = builder.dataOrThrow(builder.valuesType());
        this.textifier = builder.dataOrThrow(builder.textifierType());

        this.widget = ButtonWidget.builder(this.textifier.apply(this.value()), button -> {
            int index = this.values.indexOf(value());
            index = index >= this.values.size() - 1 ? 0 : index + 1;
            this.value(this.values.get(index));
        }).size(86,20).build();

        this.listenToChange((oldValue, newValue) -> this.widget.setMessage(this.textifier.apply(newValue)));
    }

    @Override
    public void rebuildPositions(SquareData self, SquareData parent) {
        super.rebuildPositions(self, parent);

        this.widget.setX(self.endX() - (resetButton.visible ? 108 : 87));
        this.widget.setY(self.y() + 1);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(client.textRenderer, displayName(mouseX, mouseY), pos.x(), pos.y() + 7, -1);
        this.widget.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void resetToDefault(T def) {
        this.value(def);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(widget, resetButton);
    }
}
