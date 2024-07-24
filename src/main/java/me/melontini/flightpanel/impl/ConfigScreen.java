package me.melontini.flightpanel.impl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.*;
import java.util.stream.Collectors;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.flightpanel.api.builders.CategoryBuilder;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.elements.AbstractConfigElement;
import me.melontini.flightpanel.api.util.ConfigScreenProxy;
import me.melontini.flightpanel.impl.widgets.ConfigElementListWidget;
import me.melontini.flightpanel.impl.widgets.OptionInfoWidget;
import me.melontini.flightpanel.impl.widgets.tab.TabManager;
import me.melontini.flightpanel.impl.widgets.tab.TabNavigationWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class ConfigScreen extends Screen implements ConfigScreenProxy {

  private static final Text SAVE_LABEL = Text.translatable("service.flight-panel.widget.save");
  private static final Text SAVE_ERROR_TITLE =
      Text.translatable("service.flight-panel.widget.save.error.title");
  private static final Text SAVE_ERROR_DESC = Text.translatable(
          "service.flight-panel.widget.save.error.description")
      .formatted(Formatting.GRAY);

  private static final Text BACK_LABEL = Text.translatable("service.flight-panel.widget.back");

  private static final Text CHANGES_TITLE =
      Text.translatable("service.flight-panel.screen.unsaved_changes.title");
  private static final Text CHANGES_MSG =
      Text.translatable("service.flight-panel.screen.unsaved_changes.message");
  private static final Text CHANGES_YES =
      Text.translatable("service.flight-panel.screen.unsaved_changes.yes");
  private static final Text CHANGES_NO =
      Text.translatable("service.flight-panel.screen.unsaved_changes.no");

  private static final Text RESTART_TITLE =
      Text.translatable("service.flight-panel.screen.restart_required.title");
  private static final Text RESTART_MSG =
      Text.translatable("service.flight-panel.screen.restart_required.message");
  private static final Text RESTART_YES =
      Text.translatable("service.flight-panel.screen.restart_required.yes");
  private static final Text RESTART_NO =
      Text.translatable("service.flight-panel.screen.restart_required.no");

  private final Screen parent;
  private final Runnable saveFunction;

  private final Map<Text, ConfigElementListWidget> categories;
  private final Collection<AbstractConfigElement<?, ?>> allChildren;

  private final TabManager tabManager;
  private TabNavigationWidget navigationWidget;
  private ButtonWidget backWidget;
  private ButtonWidget saveWidget;
  private OptionInfoWidget optionInfoWidget;

  private ConfigElementListWidget currentCategory;
  boolean edited = false;
  boolean erroring = false;
  boolean requiresRestart = false;

  public ConfigScreen(
      Text title, Screen parent, Map<Text, CategoryBuilder> children, Runnable saveFunction) {
    super(title);
    this.parent = parent;
    this.saveFunction = saveFunction;

    this.categories = new LinkedHashMap<>(Maps.transformValues(
        children,
        input -> new ConfigElementListWidget(input.build().stream()
            .<AbstractConfigElement<?, ?>>map(BaseElementBuilder::build)
            .toList())));
    this.allChildren = this.categories.values().stream()
        .flatMap(w -> w.children().stream())
        .collect(Collectors.toUnmodifiableList());
    this.currentCategory = this.categories.values().stream()
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException("Config screens must contain at least 1 category!"));

    this.tabManager = new TabManager(text -> {
      this.remove(this.currentCategory);
      this.currentCategory = this.categories.get(text);
      this.addDrawableChild(this.currentCategory);
      this.currentCategory.rebuildPositions();
    });
  }

  @Override
  public void init() {
    int wHeight = client.getWindow().getScaledHeight();
    int wWidth = client.getWindow().getScaledWidth();

    this.navigationWidget = TabNavigationWidget.builder(this.tabManager, getViewBoxWidth())
        .tabs(this.categories.keySet().toArray(Text[]::new))
        .build();

    for (ConfigElementListWidget value : this.categories.values()) {
      value.dimensions(
          0, getHeaderSize(), getViewBoxWidth(), wHeight - getFooterSize() - getHeaderSize());
      value.rebuildPositions();
    }

    int backLabelWidth = client.textRenderer.getWidth(BACK_LABEL);
    this.backWidget = ButtonWidget.builder(BACK_LABEL, button -> this.close())
        .position(3, getViewBoxBottom() + 3)
        .size(backLabelWidth + 16, 20)
        .build();

    int saveLabelWidth = client.textRenderer.getWidth(SAVE_LABEL);
    this.saveWidget = ButtonWidget.builder(SAVE_LABEL, button -> {
          if (!this.requiresRestart)
            this.requiresRestart =
                this.allChildren.stream().anyMatch(e -> e.requiresRestart() && e.modified());

          this.allChildren.forEach(AbstractConfigElement::save);
          this.saveFunction.run();
        })
        .position(3 + backLabelWidth + 16 + 3, getViewBoxBottom() + 3)
        .size(saveLabelWidth + 16, 20)
        .build();
    this.saveWidget.active = false;

    var optionInfo = new OptionInfoWidget(
        getViewBoxWidth(),
        getHeaderSize(),
        wWidth - getViewBoxWidth(),
        wWidth - getFooterSize() - getHeaderSize());
    if (this.optionInfoWidget != null) optionInfo.display(this.optionInfoWidget.display());
    this.optionInfoWidget = optionInfo;

    this.addDrawableChild(this.navigationWidget);
    this.addDrawableChild(this.currentCategory);
    this.addDrawableChild(this.backWidget);
    this.addDrawableChild(this.saveWidget);
    this.addDrawableChild(this.optionInfoWidget);

    this.navigationWidget.selectTab(0, false);
    this.navigationWidget.init();
    this.updateWidgets();
  }

  private void updateWidgets() {
    this.saveWidget.active = this.edited && !this.erroring;
    this.saveWidget.setMessage(erroring ? SAVE_LABEL.copy().formatted(Formatting.RED) : SAVE_LABEL);
  }

  private final Deque<Runnable> renderTasks = new ArrayDeque<>();

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.renderBackgroundTexture(context);

    context.fill(0, 0, this.width, getHeaderSize(), ColorUtil.toColor(0, 0, 0, 130));
    context.fill(0, getViewBoxBottom(), this.width, this.height, ColorUtil.toColor(0, 0, 0, 130));

    context.drawTexture(
        CreateWorldScreen.FOOTER_SEPARATOR_TEXTURE,
        0,
        MathHelper.roundUpToMultiple(this.height - getFooterSize() - 2, 2),
        0.0F,
        0.0F,
        this.width,
        2,
        32,
        2);
    super.render(context, mouseX, mouseY, delta);
    context.drawTexture(
        CreateWorldScreen.HEADER_SEPARATOR_TEXTURE, 0, 24 - 2, 0.0F, 0.0F, this.width, 2, 32, 2);

    if (this.categories.size() <= 1)
      context.drawCenteredTextWithShadow(
          this.textRenderer, this.title, this.width / 2, 12 - 4, 16777215);

    if (this.erroring
        && saveWidget.visible
        && mouseX >= saveWidget.getX()
        && mouseX <= saveWidget.getX() + saveWidget.getWidth()
        && mouseY >= saveWidget.getY()
        && mouseY <= saveWidget.getY() + saveWidget.getHeight()) {
      context.drawTooltip(
          client.textRenderer, List.of(SAVE_ERROR_TITLE, SAVE_ERROR_DESC), mouseX, mouseY);
    }

    synchronized (this.renderTasks) {
      while (!this.renderTasks.isEmpty()) {
        Runnable runnable = this.renderTasks.poll();
        if (runnable != null) runnable.run();
      }
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
    return 24;
  }

  public int getFooterSize() {
    return 26;
  }

  @Override
  public void tick() {
    for (AbstractConfigElement<?, ?> child : this.allChildren) child.tick();

    this.edited = this.allChildren.stream().anyMatch(AbstractConfigElement::modified);
    this.erroring =
        this.allChildren.stream().anyMatch(element -> element.getElementError() != null);

    this.updateWidgets();
  }

  @Override
  public void close() {
    MakeSure.notNull(client);

    if (this.allChildren.stream().anyMatch(AbstractConfigElement::modified)) {
      this.client.setScreen(new ConfirmScreen(
          response -> {
            if (response) {
              this.saveWidget.onPress();
              this.close(); // We have to check requiresRestart
            } else this.client.setScreen(this.parent);
          },
          CHANGES_TITLE,
          CHANGES_MSG,
          CHANGES_YES,
          CHANGES_NO));
      return;
    }

    if (this.requiresRestart) {
      this.client.setScreen(new ConfirmScreen(
          response -> {
            if (response) this.client.scheduleStop();
            else this.client.setScreen(this.parent);
          },
          RESTART_TITLE,
          RESTART_MSG,
          RESTART_YES,
          RESTART_NO));
      return;
    }
    this.client.setScreen(this.parent);
  }

  public static void fillGradientHorizontal(
      MatrixStack matrices,
      int startX,
      int startY,
      int endX,
      int endY,
      int colorStart,
      int colorEnd) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorProgram);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    fillGradientHorizontal(
        matrices.peek().getPositionMatrix(),
        bufferBuilder,
        startX,
        startY,
        endX,
        endY,
        0,
        colorStart,
        colorEnd);
    tessellator.draw();
    RenderSystem.disableBlend();
  }

  public static void fillGradientHorizontal(
      Matrix4f matrix,
      BufferBuilder builder,
      int startX,
      int startY,
      int endX,
      int endY,
      int z,
      int colorStart,
      int colorEnd) {
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

  @Override
  public boolean isPointWithinListBounds(double x, double y) {
    return this.currentCategory.isMouseOver(x, y);
  }

  @Override
  public void queuePostElementRender(Runnable runnable) {
    synchronized (this.renderTasks) {
      this.renderTasks.add(runnable);
    }
  }
}
