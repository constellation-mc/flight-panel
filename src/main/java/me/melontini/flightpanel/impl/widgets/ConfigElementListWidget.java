package me.melontini.flightpanel.impl.widgets;

import com.google.common.collect.ImmutableList;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.util.SquareData;
import me.melontini.flightpanel.impl.util.ConfigScreenProxy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Accessors(fluent = true)
public class ConfigElementListWidget extends AbstractParentElement implements Drawable, Selectable {

    @Setter
    public int x, y, width, height;

    private int scrollPos = 0;
    private int maxScrollPos = 0;

    private final List<AbstractConfigElement<?, ?>> children;
    private final List<AbstractConfigElement<?, ?>> visibleChildren = new ArrayList<>();

    public ConfigElementListWidget(List<AbstractConfigElement<?, ?>> children) {
        this.children = ImmutableList.copyOf(children);
    }

    public void rebuildPositions() {
        var wd = SquareData.of(this);

        int h = y + 2;
        for (AbstractConfigElement<?, ?> child : this.children()) {
            child.rebuildPositions(SquareData.of(x + 10, h + scrollPos, width - 21, child.getBaseElementHeight()), wd);
            h += child.getElementHeight() + 1;
        }
        h += 1;
        this.maxScrollPos = h > y + height ? h - (y + height) : 0;
        int oldScrollPos = this.scrollPos;
        this.scrollPos = MathHelper.clamp(this.scrollPos, -this.maxScrollPos, 0);

        if (oldScrollPos != this.scrollPos) {
            this.rebuildPositions();
            return;
        }

        this.visibleChildren.clear();
        for (AbstractConfigElement<?, ?> child : this.children()) {
            if (child.pos().withHeight(child.getElementHeight()).intersects(wd)) visibleChildren.add(child);
        }
        //System.out.printf("%s %s%n", this.visibleChildren.size(), this.children.size());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + width, y + height, ColorUtil.toColor(0, 0, 0, 100));

        if (this.maxScrollPos > 0) {
            int scroller = MathHelper.clamp((height * height) / this.maxScrollPos, height / 4, height / 2);
            int scrollerPos = Math.abs(this.scrollPos) * (height - scroller) / this.maxScrollPos + y;

            int x = this.x + width;

            context.fill(x-6, y, x, y + height, -16777216);
            context.fill(x-6, scrollerPos, x, scrollerPos + scroller, -8355712);
            context.fill(x-6, scrollerPos, x - 1, scrollerPos + scroller - 1, -4144960);
        }

        var hovered = hoveredElement(mouseX, mouseY).filter(e -> e instanceof AbstractConfigElement<?,?>).map(e -> ((AbstractConfigElement<?, ?>) e));

        context.enableScissor(x, y, x + width, y + height);
        hovered.ifPresent(abstractConfigElement -> abstractConfigElement.renderMouseHover(context, mouseX));
        for (AbstractConfigElement<?, ?> child : this.visibleChildren) {
            child.render(context, mouseX, mouseY, delta);
        }
        context.disableScissor();

        if (hovered.isPresent()) {
            var errorTooltip = hovered.get().getElementError();
            if (errorTooltip != null) hovered.get().renderErrorTooltip(context, mouseX, mouseY, errorTooltip);
        }
    }

    private boolean lastDrag = false;

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (super.mouseScrolled(mouseX, mouseY, amount)) return true;

        int scrollPos = MathHelper.clamp(this.scrollPos + (int) (amount * 10), -this.maxScrollPos, 0);
        if (this.scrollPos != scrollPos) {
            this.scrollPos = scrollPos;
            this.rebuildPositions();
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.maxScrollPos <= 0 || button != 0 || !lastDrag) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        int j = MathHelper.clamp(((height * height) / this.maxScrollPos), height / 4, height / 2);
        double e = Math.max(1.0, (double) this.maxScrollPos / (height - j));

        int scrollPos = MathHelper.clamp((int) (this.scrollPos - (deltaY * e)), -this.maxScrollPos, 0);
        if (this.scrollPos != scrollPos) {
            this.scrollPos = scrollPos;
            this.rebuildPositions();
        }
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.setFocused(null);
        this.children().forEach(AbstractConfigElement::unfocus);
        if (!isMouseOver(mouseX, mouseY)) return false;

        if (button == 0 && mouseX >= x + width - 6 && mouseX <= x + width) {
            if (mouseY >= y && mouseY <= y + height) {
                lastDrag = true;
                return true;
            }
        }
        if (lastDrag) return true;

        for (AbstractConfigElement<?, ?> child : this.visibleChildren) {
            var r = child.hoveredChildOrSelf((int) mouseX, (int) mouseY);
            if (r == null) r = child;

            if (!r.mouseClicked(mouseX, mouseY, button)) continue;
            this.proxy().setDisplayEntry(r);
            this.setFocused(r);

            if (button == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (lastDrag) lastDrag = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        if (!this.isMouseOver(mouseX, mouseY)) return Optional.empty();

        for(AbstractConfigElement<?, ?> element : this.visibleChildren) {
            var hover = element.hoveredChildOrSelf((int) mouseX, (int) mouseY);
            if (hover != null) return Optional.of(hover);
        }

        return Optional.empty();
    }

    public void dimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ConfigScreenProxy proxy() {
        if (MinecraftClient.getInstance().currentScreen instanceof ConfigScreenProxy csp) return csp;
        throw new IllegalStateException();
    }

    //TODO
    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public List<? extends AbstractConfigElement<?, ?>> children() {
        return this.children;
    }
}
