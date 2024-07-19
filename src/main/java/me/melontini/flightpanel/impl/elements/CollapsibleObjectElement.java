package me.melontini.flightpanel.impl.elements;

import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.builders.elements.CollapsibleObjectBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.elements.AbstractValuedElement;
import me.melontini.flightpanel.api.util.SquareData;
import me.melontini.flightpanel.impl.widgets.IconDrawer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CollapsibleObjectElement<T> extends AbstractValuedElement<T, CollapsibleObjectElement<T>> {

    private static final Identifier ICONS = Identifier.of("flight-panel", "textures/gui/gui_icons.png");

    private final List<AbstractConfigElement<?, ?>> children;

    private boolean collapsed;
    private final List<AbstractConfigElement<?, ?>> visibleChildren = new ArrayList<>();
    private final IconDrawer iconDrawer;

    public CollapsibleObjectElement(CollapsibleObjectBuilder<T> builder) {
        super(builder);
        this.resetButton.visible = false;

        this.children = builder.dataOrThrow(CollapsibleObjectBuilder.ELEMENTS).stream().<AbstractConfigElement<?, ?>>map(BaseElementBuilder::build).toList();
        this.collapsed = builder.dataOrElse(CollapsibleObjectBuilder.COLLAPSED, true);
        this.iconDrawer = IconDrawer.builder()
                .width(12).height(12).u(0).v(0)
                .textureWidth(64).textureHeight(64)
                .texture(ICONS).build();
    }

    @Override
    protected void resetToDefault(T def) {

    }

    @Override
    public void rebuildPositions(SquareData self, SquareData parent) {
        super.rebuildPositions(self, parent);

        int childY = self.endY();
        for (AbstractConfigElement<?, ?> child : this.children) {
            child.rebuildPositions(SquareData.of(self.x() + 16, childY, self.width() - 16, child.getBaseElementHeight()), parent);
            childY += child.getElementHeight() + 1;
        }

        this.visibleChildren.clear();
        if (!collapsed) {
            for (AbstractConfigElement<?, ?> child : this.children) {
                if (child.pos().withHeight(child.getElementHeight()).intersects(parent)) visibleChildren.add(child);
            }
        }
        this.iconDrawer.x(self.x() + 1).y(self.y() + 4).u(collapsed ? 12 : 0);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.visibleChildren.forEach(element -> element.render(context, mouseX, mouseY, delta));

        var dn = displayName(mouseX, mouseY);
        int color = Optional.ofNullable(dn.getStyle().getColor()).map(TextColor::getRgb).orElse(-1);
        this.iconDrawer.color(color).renderIcon(context, true);
        context.drawTextWithShadow(client.textRenderer, dn, pos.x() + 12 + 4, pos.y() + 7, -1);
    }

    @Override
    public void tick() {
        super.tick();
        this.children.forEach(AbstractConfigElement::tick);
    }

    @Override
    public void unfocus() {
        super.unfocus();
        this.children.forEach(AbstractConfigElement::unfocus);
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

        for (AbstractConfigElement<?, ?> cell : this.visibleChildren) {
            r = cell.hoveredChildOrSelf(mouseX, mouseY);
            if (r != null) return r;
        }
        return null;
    }

    @Override
    public boolean requiresRestart() {
        return this.children.stream().anyMatch(AbstractConfigElement::requiresRestart);
    }

    @Override
    public @Nullable Text getElementError() {
        List<Text> texts = this.children.stream().map(AbstractConfigElement::getElementError).filter(Objects::nonNull).toList();

        if (texts.size() == 1) return texts.get(0);
        if (texts.size() > 1) return Text.translatable("service.flight-panel.error.list.multiple");
        return null;
    }

    @Override
    public boolean modified() {
        return this.children.stream().anyMatch(AbstractConfigElement::modified);
    }

    @Override
    public int getElementHeight() {
        return getBaseElementHeight() + children().stream().mapToInt(value -> value.getElementHeight() + 1).sum();
    }

    @Override
    public void save() {
        this.children.forEach(AbstractConfigElement::save);
    }

    @Override
    public List<? extends AbstractConfigElement<?, ?>> children() {
        return !collapsed ? this.children : Collections.emptyList();
    }
}
