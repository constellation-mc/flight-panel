package me.melontini.flightpanel.api.elements;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.util.SquareData;
import me.melontini.flightpanel.impl.ConfigScreen;
import me.melontini.flightpanel.impl.util.ConfigScreenProxy;
import me.melontini.flightpanel.impl.util.TextUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Accessors(fluent = true)
public abstract class AbstractConfigElement<T, S extends AbstractConfigElement<T, S>> extends AbstractParentElement implements ParentElement, Selectable {

    private static final int HIGHLIGHT_START = ColorUtil.toColor(255, 255, 255, 0);
    private static final int HIGHLIGHT_END = ColorUtil.toColor(255, 255, 255, 90);

    @NotNull protected final MinecraftClient client = MinecraftClient.getInstance();

    @Getter
    private final Text elementName;
    @Getter
    private final boolean elementNameEmpty;
    private final boolean requiresRestart;
    private final @Nullable List<Text> elementDescription;

    @Getter
    protected SquareData pos;

    public AbstractConfigElement(BaseElementBuilder<T, S, ?> builder) {
        this.elementName = builder.dataOrThrow(BaseElementBuilder.ELEMENT_NAME);
        this.elementNameEmpty = TextUtil.isEmpty(this.elementName());

        this.requiresRestart = builder.dataOrElse(BaseElementBuilder.REQUIRES_RESTART, false);

        List<Text> generatedDesc = builder.data(BaseElementBuilder.DESCRIPTION);
        if (generatedDesc == null) {
            if (elementName.getContent() instanceof TranslatableTextContent ttc) {
                String key = (ttc.getKey().endsWith(".") ? ttc.getKey().substring(0, ttc.getKey().length() - 1) : ttc.getKey()) + ".@Tooltip";
                if (I18n.hasTranslation(key)) generatedDesc = Collections.singletonList(Text.translatable(key));
            }
        }
        this.elementDescription = generatedDesc;
    }

    /**
     * This method's only purpose is to update element positions. Do not do anything expensive here!
     * <p>
     * Usually this method is called when scroll position updates or elements expand/retract.
     */
    public void rebuildPositions(SquareData self, SquareData parent) {
        this.pos = self;
    }

    public int firstHighlightTarget = 0;
    public int secondHighlightTarget = 0;

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            int relMouseX = MathHelper.clamp(mouseX, pos.x(), pos.endX());

            firstHighlightTarget = relMouseX;
            secondHighlightTarget = relMouseX;
        }
    }

    public void renderMouseHover(DrawContext context, int mouseX) {
        int relMouseX = MathHelper.clamp(mouseX, pos.x(), pos.endX());

        this.firstHighlightTarget = MathHelper.lerp(0.5f * client.getLastFrameDuration(), this.firstHighlightTarget, this.pos.x());
        this.secondHighlightTarget = MathHelper.lerp(0.5f * client.getLastFrameDuration(), this.secondHighlightTarget, this.pos.endX());

        ConfigScreen.fillGradientHorizontal(context.getMatrices(), this.firstHighlightTarget, this.pos.y(), relMouseX, this.pos.y() + 1, HIGHLIGHT_START, HIGHLIGHT_END);
        ConfigScreen.fillGradientHorizontal(context.getMatrices(), relMouseX, this.pos.y(), this.secondHighlightTarget, this.pos.y() + 1, HIGHLIGHT_END, HIGHLIGHT_START);

        ConfigScreen.fillGradientHorizontal(context.getMatrices(), this.firstHighlightTarget, this.pos.endY() - 1, relMouseX, this.pos.endY(), HIGHLIGHT_START, HIGHLIGHT_END);
        ConfigScreen.fillGradientHorizontal(context.getMatrices(), relMouseX, this.pos.endY() - 1, this.secondHighlightTarget, this.pos.endY(), HIGHLIGHT_END, HIGHLIGHT_START);
    }

    public void renderErrorTooltip(DrawContext context, int mouseX, int mouseY, Text errorTooltip) {
        context.drawTooltip(client.textRenderer, errorTooltip.copy().formatted(Formatting.RED), mouseX, mouseY);
    }

    public void tick() {
    }

    public void save() {

    }

    public S self() {
        return (S) this;
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    public Text displayName(int mouseX, int mouseY) {
        MutableText text = elementName().copy().formatted(Formatting.GRAY);
        if (this.isMouseOver(mouseX, mouseY)) text.formatted(Formatting.WHITE);
        if (modified()) text.formatted(Formatting.ITALIC);
        if (getElementError() != null) {
            if (this.elementNameEmpty) text.append(Text.translatable("service.flight-panel.error.widget.generic"));
            text.formatted(Formatting.RED);
        }
        return text;
    }

    public Collection<Text> description() {
        return this.elementDescription != null ? this.elementDescription : Collections.emptyList();
    }

    public @Nullable Text getElementError() {
        return null;
    }

    public int getBaseElementHeight() {
        return 22;
    }

    public int getElementHeight() {
        return getBaseElementHeight();
    }

    public boolean modified() {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return pos.withinBounds((int) mouseX, (int) mouseY);
    }

    public void unfocus() {
        this.setFocused(null);
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (isMouseOver(mouseX, mouseY)) return this.onWidgetClicked(mouseX, mouseY, button);
        return false;
    }

    protected boolean onWidgetClicked(double mouseX, double mouseY, int button) {
        return button == 0;
    }

    //TODO I have no idea how to make narrations work proper.
    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, elementName());
    }

    public @Nullable AbstractConfigElement<?, ?> hoveredChildOrSelf(int mouseX, int mouseY) {
        return isMouseOver(mouseX, mouseY) ? this : null;
    }

    protected ConfigScreenProxy proxy() {
        if (MinecraftClient.getInstance().currentScreen instanceof ConfigScreenProxy csp) return csp;
        throw new IllegalStateException();
    }
}
