package me.melontini.flightpanel.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.flightpanel.api.builders.CategoryBuilder;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.impl.util.ConfigScreenProxy;
import me.melontini.flightpanel.impl.widgets.ConfigElementListWidget;
import me.melontini.flightpanel.impl.widgets.OptionInfoWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigScreen extends Screen implements ConfigScreenProxy {

    private static final Text SAVE_LABEL = Text.translatable("service.flight-panel.widget.save_and_exit");
    private static final Text SAVE_ERROR_TITLE = Text.translatable("service.flight-panel.widget.save_and_exit.error.title");
    private static final Text SAVE_ERROR_DESC = Text.translatable("service.flight-panel.widget.save_and_exit.error.description").formatted(Formatting.GRAY);
    private static final Text REQ_RESTART_TITLE = Text.translatable("service.flight-panel.screen.restart_required.title");
    private static final Text REQ_RESTART_MSG = Text.translatable("service.flight-panel.screen.restart_required.message");
    private static final Text REQ_RESTART_Y = Text.translatable("service.flight-panel.screen.restart_required.yes");
    private static final Text REQ_RESTART_N = Text.translatable("service.flight-panel.screen.restart_required.no");

    private final Screen parent;
    private final Runnable saveFunction;

    private final Map<Text, ConfigElementListWidget> categories;
    //private final List<Text> indexedCategories;
    private final Collection<AbstractConfigElement<?, ?>> allChildren;

    private ButtonWidget saveAndExit;
    private OptionInfoWidget optionInfoWidget;

    private ConfigElementListWidget currentCategory;
    boolean edited = false;
    boolean erroring = false;

    public ConfigScreen(Text title, Screen parent, Collection<CategoryBuilder> children, Runnable saveFunction) {
        super(title);
        this.parent = parent;
        this.saveFunction = saveFunction;

        this.categories = children.stream().collect(Collectors.toMap(CategoryBuilder::title, b -> new ConfigElementListWidget(b.stream().<AbstractConfigElement<?, ?>>map(BaseElementBuilder::build).toList())));
        //this.indexedCategories = this.categories.keySet().stream().toList();
        this.allChildren = this.categories.values().stream().flatMap(w -> w.children().stream()).collect(Collectors.toUnmodifiableList());
        this.currentCategory = this.categories.values().stream().findFirst().orElseThrow(() -> new RuntimeException("Config screens must contain at least 1 category!"));
    }

    @Override
    public void init() {
        int wHeight = client.getWindow().getScaledHeight();
        int wWidth = client.getWindow().getScaledWidth();

        for (ConfigElementListWidget value : this.categories.values()) {
            value.dimensions(0, getHeaderSize(), getViewBoxWidth(), wHeight - getFooterSize() - getHeaderSize());
            value.rebuildPositions();
        }

        int saveLabelWidth = client.textRenderer.getWidth(SAVE_LABEL);
        this.saveAndExit = ButtonWidget.builder(SAVE_LABEL, button -> {
            this.allChildren.forEach(AbstractConfigElement::save);
            this.saveFunction.run();

            for (AbstractConfigElement<?, ?> child : this.allChildren) {
                if (!child.requiresRestart() || !child.modified()) continue;
                this.client.setScreen(new ConfirmScreen(t -> {
                    if (t) this.client.scheduleStop();
                    else this.client.setScreen(this.parent);
                }, REQ_RESTART_TITLE, REQ_RESTART_MSG, REQ_RESTART_Y, REQ_RESTART_N));
                return;
            }
            this.client.setScreen(this.parent);
        }).position(3, getViewBoxBottom() + 3).size(saveLabelWidth + 16, 20).build();
        this.saveAndExit.active = false;

        var optionInfo = new OptionInfoWidget(getViewBoxWidth(), getHeaderSize(), wWidth - getViewBoxWidth(), wWidth - getFooterSize() - getHeaderSize());
        if (this.optionInfoWidget != null) optionInfo.display(this.optionInfoWidget.display());
        this.optionInfoWidget = optionInfo;

        this.addDrawableChild(this.currentCategory);
        this.addDrawableChild(this.saveAndExit);
        this.addDrawableChild(this.optionInfoWidget);

        this.updateWidgets();
    }

    private void updateWidgets() {
        this.saveAndExit.active = this.edited && !this.erroring;
        this.saveAndExit.setMessage(erroring ? SAVE_LABEL.copy().formatted(Formatting.RED) : SAVE_LABEL);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        super.render(context, mouseX, mouseY, delta);

        int hrStart = ColorUtil.toColor(255, 255, 255, 50);
        int hrEnd = ColorUtil.toColor(255, 255, 255, 125);
        int halfWidth = client.getWindow().getScaledWidth() / 2;

        fillGradientHorizontal(context.getMatrices(), 0, getHeaderSize() - 1, halfWidth, getHeaderSize(), hrStart, hrEnd);
        fillGradientHorizontal(context.getMatrices(), halfWidth, getHeaderSize() - 1, client.getWindow().getScaledWidth(), getHeaderSize(), hrEnd, hrStart);
        fillGradientHorizontal(context.getMatrices(), 0, client.getWindow().getScaledHeight() - getFooterSize(), halfWidth, client.getWindow().getScaledHeight() - getFooterSize() + 1, hrStart, hrEnd);
        fillGradientHorizontal(context.getMatrices(), halfWidth, client.getWindow().getScaledHeight() - getFooterSize(), client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight() - getFooterSize() + 1, hrEnd, hrStart);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, (getHeaderSize() / 2) - 3, 16777215);

        if (this.erroring && saveAndExit.visible
                && mouseX >= saveAndExit.getX()
                && mouseX <= saveAndExit.getX() + saveAndExit.getWidth()
                && mouseY >= saveAndExit.getY()
                && mouseY <= saveAndExit.getY() + saveAndExit.getHeight()) {
            context.drawTooltip(client.textRenderer, List.of(SAVE_ERROR_TITLE, SAVE_ERROR_DESC), mouseX, mouseY);
        }
    }

    public int getViewBoxWidth() {
        return (int) (client.getWindow().getScaledWidth() / 1.45);
    }

    public int getViewBoxTop() {
        return getHeaderSize();
    }

    public int getViewBoxBottom() {
        return client.getWindow().getScaledHeight() - getFooterSize();
    }

    public int getHeaderSize() {
        return 26;
    }

    public int getFooterSize() {
        return 26;
    }

    @Override
    public void tick() {
        for (AbstractConfigElement<?, ?> child : this.allChildren) child.tick();

        this.edited = this.allChildren.stream().anyMatch(AbstractConfigElement::modified);
        this.erroring = this.allChildren.stream().anyMatch(element -> element.getElementError() != null);

        this.updateWidgets();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        super.setFocused(focused);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(this.currentCategory, this.saveAndExit, this.optionInfoWidget);
    }

    public static void fillGradientHorizontal(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        fillGradientHorizontal(matrices.peek().getPositionMatrix(), bufferBuilder, startX, startY, endX, endY, 0, colorStart, colorEnd);
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    public static void fillGradientHorizontal(Matrix4f matrix, BufferBuilder builder, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
        float f = ColorUtil.getAlphaF(colorStart);
        float g = ColorUtil.getRedF(colorStart);
        float h = ColorUtil.getGreenF(colorStart);
        float i = ColorUtil.getBlueF(colorStart);
        float j = ColorUtil.getAlphaF(colorEnd);
        float k = ColorUtil.getRedF(colorEnd);
        float l = ColorUtil.getGreenF(colorEnd);
        float m = ColorUtil.getBlueF(colorEnd);

        builder.vertex(matrix, endX, startY, z).color(k, l, m, j).next();
        builder.vertex(matrix, startX, startY, z).color(g, h, i, f).next();
        builder.vertex(matrix, startX, endY, z).color(g, h, i, f).next();
        builder.vertex(matrix, endX, endY, z).color(k, l, m, j).next();
    }

    @Override
    public void setDisplayEntry(AbstractConfigElement<?, ?> display) {
        this.optionInfoWidget.display(display);
    }

    public void rebuildPositions() {
        this.currentCategory.rebuildPositions();
    }
}
