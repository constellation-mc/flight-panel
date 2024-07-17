package me.melontini.flightpanel.api.elements;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.flightpanel.api.builders.elements.ValuedElementBuilder;
import me.melontini.flightpanel.api.util.SquareData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Accessors(fluent = true) @Getter @Setter
public abstract class AbstractValuedElement<T, S extends AbstractValuedElement<T, S>> extends AbstractConfigElement<T, S> {

    private static final Identifier ICONS = Identifier.of("flight-panel", "textures/gui/gui_icons.png");

    private T original;
    private T value;

    protected final Supplier<@NotNull T> defaultValue;
    protected boolean modified = false;
    private final @Nullable Consumer<T> saveFunction;

    private List<BiConsumer<T, T>> changeListeners = null;

    protected final BiFunction<T, T, Boolean> deepEquals;
    protected final ButtonWidget resetButton;

    public AbstractValuedElement(ValuedElementBuilder<T, S, ?> builder) {
        super(builder);
        this.original = builder.dataOrThrow(builder.valueType());
        this.value = builder.dataOrThrow(builder.valueType());
        this.defaultValue = builder.data(builder.defaultValueType());
        this.saveFunction = builder.data(builder.saveFunctionType());

        this.deepEquals = value.getClass().isArray() ? (t1, t2) -> Arrays.deepEquals((Object[]) t1, (Object[]) t2) : Objects::equals;
        this.modified = !equals(original, value);

        this.resetButton = ButtonWidget.builder(Text.empty(), button -> {
            if (defaultValue != null) this.resetToDefault(defaultValue.get());
        }).size(20, 20).build();
        this.resetButton.visible = defaultValue != null;

        if (defaultValue != null) {
            this.resetButton.active = !equals(defaultValue.get(), value());
            this.listenToChange((t, t2) -> this.resetButton.active = !equals(defaultValue.get(), t2));
        }
    }

    protected abstract void resetToDefault(T def);

    @Override
    public void rebuildPositions(SquareData self, SquareData parent) {
        super.rebuildPositions(self, parent);

        this.resetButton.setX(self.endX() - 21);
        this.resetButton.setY(self.y() + 1);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.resetButton.render(context, mouseX, mouseY, delta);

        if (this.resetButton.visible) {
            int ix = this.resetButton.getX();
            int iy = this.resetButton.getY();

            int color = this.resetButton.active ? 16777215 : 10526880;

            if (this.resetButton.active) {
                context.setShaderColor(ColorUtil.getRedF(color) * 0.25F, ColorUtil.getGreenF(color) * 0.25F, ColorUtil.getBlueF(color) * 0.25F, 1);
                context.drawTexture(ICONS, ix + 5, iy + 5, 0, 0, 24, 12, 12, 64, 64);
            }
            context.setShaderColor(ColorUtil.getRedF(color), ColorUtil.getGreenF(color), ColorUtil.getBlueF(color), 1);
            context.drawTexture(ICONS, ix + 4, iy + 4, 0, 0, 24, 12, 12, 64, 64);

            context.setShaderColor(1, 1, 1, 1);
        }
    }

    protected boolean equals(T value, T other) {
        return deepEquals.apply(value, other);
    }

    public void value(@NotNull T value) {
        if (this.changeListeners != null && !equals(this.value, value)) {
            for (BiConsumer<T, T> c : changeListeners) {
                c.accept(this.value, value);
            }
        }
        this.modified = !equals(this.original, value);
        this.value = value;
    }

    public void listenToChange(BiConsumer<T, T> consumer) {
        if (changeListeners == null) changeListeners = new ArrayList<>();
        changeListeners.add(consumer);
    }

    @Override
    public void save() {
        if (saveFunction != null) saveFunction.accept(value());

        this.original = value();
        this.modified = !equals(this.original, value);
    }
}
