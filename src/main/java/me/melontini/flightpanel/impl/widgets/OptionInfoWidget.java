package me.melontini.flightpanel.impl.widgets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.impl.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.List;

@Accessors(fluent = true)
public class OptionInfoWidget implements Drawable, Element, Selectable {

    public static final Text NOTHING_SELECTED = Text.translatable("service.flight-panel.widget.option_info.nothing_selected").formatted(Formatting.GRAY);

    private final MinecraftClient client = MinecraftClient.getInstance();

    @Getter @Setter
    private int x, y, width, height;
    @Getter
    private AbstractConfigElement<?, ?> display;
    private List<OrderedText> optionTitle;
    private List<OrderedText> optionDescription;

    public OptionInfoWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int y = this.y + 8;
        context.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        if (display == null) {
            context.drawText(client.textRenderer, NOTHING_SELECTED, x + 8, y, -1, true);
            context.disableScissor();
            return;
        }

        for (OrderedText text : this.optionTitle) {
            context.drawText(client.textRenderer, text, x + 8, y, -1, true);
            y += 9;
        }

        if (!optionDescription.isEmpty()) {
            y += 6;
            for (OrderedText text : this.optionDescription) {
                context.drawText(client.textRenderer, text, x + 8, y, -1, true);
                y += 11;
            }
        }
        context.disableScissor();
    }

    public void display(AbstractConfigElement<?, ?> display) {
        if (display == null || TextUtil.isBlank(display.elementName())) {
            this.display = null;
            this.optionTitle = null;
            this.optionDescription = null;
            return;
        }
        this.display = display;
        this.optionTitle = client.textRenderer.wrapLines(display.elementName().copy().formatted(Formatting.BOLD), width - 8 - 8);
        this.optionDescription = display.description().stream().map(text -> client.textRenderer.wrapLines(text, width - 8 - 8)).flatMap(Collection::stream).toList();
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
