package me.melontini.flightpanel.impl.elements.collections;

import me.melontini.flightpanel.api.builders.elements.CollapsibleObjectBuilder;
import me.melontini.flightpanel.api.builders.elements.collections.NestedListBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.SquareData;
import me.melontini.flightpanel.impl.widgets.IconDrawer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class NestedListElement<T> extends AbstractValuedElement<List<T>, NestedListElement<T>> {

    private static final Identifier ICONS = Identifier.of("flight-panel", "textures/gui/gui_icons.png");

    private final List<NestedCell> children = new ArrayList<>();
    private final ButtonWidget newElementButton;
    private final boolean immutable;
    private final BiFunction<T, NestedListElement<T>, AbstractValuedElement<T, ?>> cellFactory;
    private final Supplier<T> defaultElementValue;

    private boolean collapsed;
    private final List<NestedCell> visibleChildren = new ArrayList<>();
    private final IconDrawer iconDrawer;

    public NestedListElement(NestedListBuilder<T> builder) {
        super(builder);
        this.cellFactory = builder.dataOrThrow(builder.cellFactoryType());
        this.immutable = builder.dataOrElse(NestedListBuilder.IMMUTABLE, false);
        this.defaultElementValue = builder.dataOrThrow(builder.defaultElementType());
        this.collapsed = builder.dataOrElse(CollapsibleObjectBuilder.COLLAPSED, true);

        this.newElementButton = new ButtonWidget(0,0,20,20, Text.literal("+"), button -> {
            var list = new ArrayList<>(NestedListElement.this.value());
            var cell = new NestedCell(this.cellFactory.apply(this.defaultElementValue.get(), this), list.size());
            this.children.add(cell);
            list.add(cell.element().value());//The factory/element may do addition transformations on the object.
            value(list);
            this.collapsed = false;
            this.proxy().rebuildPositions();
        }, Supplier::get) {
            @Override
            protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                this.hovered = this.hovered && NestedListElement.this.proxy().isPointWithinListBounds(mouseX, mouseY);
                super.renderButton(context, mouseX, mouseY, delta);
            }
        };
        this.newElementButton.visible = !immutable;

        for (int i = 0; i < value().size(); i++) {
            T t = value().get(i);
            this.children.add(new NestedCell(cellFactory.apply(t, this), i));
        }

        this.iconDrawer = IconDrawer.builder()
                .width(12).height(12).u(0).v(12)
                .textureWidth(64).textureHeight(64)
                .texture(ICONS).build();
    }

    @Override
    protected void resetToDefault(List<T> def) {
        this.children.clear();
        for (int i = 0; i < def.size(); i++) {
            T t = def.get(i);
            this.children.add(new NestedCell(this.cellFactory.apply(t, this), i));
        }
        value(def);
        this.proxy().rebuildPositions();
    }

    @Override
    public void rebuildPositions(SquareData self, SquareData parent) {
        super.rebuildPositions(self, parent);

        int childY = self.endY();
        for (NestedCell child : this.elements()) {
            child.element().rebuildPositions(SquareData.of(self.x() + 16, childY, self.width() - 16, child.element().getBaseElementHeight()), parent);
            childY += child.element().getElementHeight() + 1;
        }

        this.newElementButton.setX(self.endX() - (resetButton.visible ? 42 : 21));
        this.newElementButton.setY(self.y() + 1);

        this.visibleChildren.clear();
        if (!collapsed) {
            for (NestedCell child : this.children) {
                if (child.element.pos().withHeight(child.element.getElementHeight()).intersects(parent)) visibleChildren.add(child);
            }
        }
        this.iconDrawer.x(self.x() + 1).y(self.y() + 4).u(collapsed ? 12 : 0);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.visibleChildren.forEach(child -> child.render(context, mouseX, mouseY, delta));

        var dn = displayName(mouseX, mouseY);
        int color = Optional.ofNullable(dn.getStyle().getColor()).map(TextColor::getRgb).orElse(-1);
        this.iconDrawer.color(color).renderIcon(context, true);
        context.drawTextWithShadow(client.textRenderer, dn, pos.x() + 12 + 4, pos.y() + 7, -1);
        this.newElementButton.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        this.elements().forEach(NestedCell::tick);
    }

    @Override
    public void unfocus() {
        super.unfocus();
        this.children.forEach(NestedCell::unfocus);
    }

    @Override
    public boolean onWidgetClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        collapsed = !collapsed;
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.proxy().rebuildPositions();
        return true;
    }

    @Override
    public @Nullable AbstractConfigElement<?, ?> hoveredChildOrSelf(int mouseX, int mouseY) {
        var r = super.hoveredChildOrSelf(mouseX, mouseY);
        if (r != null) return r;

        for (NestedCell cell : this.visibleChildren) {
            r = cell.element().hoveredChildOrSelf(mouseX, mouseY);
            if (r != null) return r;
        }
        return null;
    }

    @Override
    public boolean requiresRestart() {
        return super.requiresRestart() || this.children.stream().anyMatch(cell -> cell.element().requiresRestart());
    }

    @Override
    public @Nullable Text getElementError() {
        List<Text> texts = this.children.stream().map(NestedCell::element).map(AbstractConfigElement::getElementError).filter(Objects::nonNull).toList();

        if (texts.size() == 1) return texts.get(0);
        if (texts.size() > 1) return Text.translatable("service.flight-panel.error.list.multiple");
        return null;
    }

    @Override
    public boolean modified() {
        return super.modified() || this.children.stream().anyMatch(cell -> cell.element().modified());
    }

    @Override
    public int getElementHeight() {
        return getBaseElementHeight() + elements().stream().mapToInt(value -> value.element().getElementHeight() + 1).sum();
    }

    @Override
    public void save() {
        this.children.forEach(cell -> cell.element().save());
        super.save();
    }

    public List<NestedCell> elements() {
        return collapsed ? Collections.emptyList() : this.children;
    }

    @Override
    public List<? extends Element> children() {
        var buttons = List.of(this.resetButton, this.newElementButton);
        return collapsed ? buttons : Stream.concat(buttons.stream(), elements().stream()).toList();
    }

    public final class NestedCell extends AbstractParentElement implements Drawable {

        private final AbstractValuedElement<T, ?> element;
        private final ButtonWidget removeWidget;
        private int index;

        private NestedCell(AbstractValuedElement<T, ?> element, int index) {
            this.element = element;
            this.index = index;

            this.element.listenToChange((t, t2) -> {
                var list = new ArrayList<>(NestedListElement.this.value());
                list.set(index(), t2);
                NestedListElement.this.value(list);
            });

            this.removeWidget = new ButtonWidget(0,0,12,12, Text.literal("-").formatted(Formatting.RED), button -> {
                var list = new ArrayList<>(NestedListElement.this.value());

                if (index() != list.size() - 1) {
                    var sub = NestedListElement.this.children.subList(index() + 1, NestedListElement.this.children.size());
                    sub.forEach(nestedCell -> nestedCell.index--);
                }
                list.remove(index());
                NestedListElement.this.children.remove(index());

                NestedListElement.this.value(list);
                NestedListElement.this.proxy().rebuildPositions();
            }, Supplier::get) {
                @Override
                protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
                    this.hovered = this.hovered && NestedListElement.this.proxy().isPointWithinListBounds(mouseX, mouseY);
                    super.renderButton(context, mouseX, mouseY, delta);
                }
            };
            this.removeWidget.visible = !NestedListElement.this.immutable;
        }

        public AbstractValuedElement<T, ?> element() {
            return element;
        }

        public int index() {
            return index;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.element.render(context, mouseX, mouseY, delta);

            this.removeWidget.setX(this.element.pos().x() - 16);
            this.removeWidget.setY(this.element.pos().y() + 5);
            this.removeWidget.render(context, mouseX, mouseY, delta);
        }

        public void unfocus() {
            this.setFocused(null);
            this.element.unfocus();
        }

        public void tick() {
            this.element.tick();
        }

        @Override
        public List<? extends Element> children() {
            return List.of(removeWidget, element);
        }
    }
}
