package dev.zenfyr.flightpanel.impl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.zenfyr.flightpanel.api.builders.CategoryBuilder;
import dev.zenfyr.flightpanel.api.builders.elements.BaseElementBuilder;
import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;
import dev.zenfyr.flightpanel.api.util.ConfigScreenProxy;
import dev.zenfyr.flightpanel.impl.widgets.ConfigElementListWidget;
import dev.zenfyr.flightpanel.impl.widgets.OptionInfoWidget;
import dev.zenfyr.flightpanel.impl.widgets.tab.TabManager;
import dev.zenfyr.flightpanel.impl.widgets.tab.TabNavigationWidget;
import dev.zenfyr.pulsar.api.util.ColorUtil;
import dev.zenfyr.pulsar.api.util.MakeSure;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class ConfigScreen extends Screen implements ConfigScreenProxy {

  private static final Component SAVE_LABEL =
      Component.translatable("service.flight-panel.widget.save");
  private static final Component SAVE_ERROR_TITLE =
      Component.translatable("service.flight-panel.widget.save.error.title");
  private static final Component SAVE_ERROR_DESC = Component.translatable(
          "service.flight-panel.widget.save.error.description")
      .withStyle(ChatFormatting.GRAY);

  private static final Component BACK_LABEL =
      Component.translatable("service.flight-panel.widget.back");

  private static final Component CHANGES_TITLE =
      Component.translatable("service.flight-panel.screen.unsaved_changes.title");
  private static final Component CHANGES_MSG =
      Component.translatable("service.flight-panel.screen.unsaved_changes.message");
  private static final Component CHANGES_YES =
      Component.translatable("service.flight-panel.screen.unsaved_changes.yes");
  private static final Component CHANGES_NO =
      Component.translatable("service.flight-panel.screen.unsaved_changes.no");

  private static final Component RESTART_TITLE =
      Component.translatable("service.flight-panel.screen.restart_required.title");
  private static final Component RESTART_MSG =
      Component.translatable("service.flight-panel.screen.restart_required.message");
  private static final Component RESTART_YES =
      Component.translatable("service.flight-panel.screen.restart_required.yes");
  private static final Component RESTART_NO =
      Component.translatable("service.flight-panel.screen.restart_required.no");

  private final Screen parent;
  private final Runnable saveFunction;

  private final Map<Component, ConfigElementListWidget> categories;
  private final Collection<AbstractConfigElement<?, ?>> allChildren;

  private final TabManager tabManager;
  private TabNavigationWidget navigationWidget;
  private Button backWidget;
  private Button saveWidget;
  private OptionInfoWidget optionInfoWidget;

  private ConfigElementListWidget currentCategory;
  boolean edited = false;
  boolean erroring = false;
  boolean requiresRestart = false;

  public ConfigScreen(
      Component title,
      Screen parent,
      Map<Component, CategoryBuilder> children,
      Runnable saveFunction) {
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
      this.removeWidget(this.currentCategory);
      this.currentCategory = this.categories.get(text);
      this.addRenderableWidget(this.currentCategory);
      this.currentCategory.rebuildPositions();
    });
  }

  @Override
  public void init() {
    int wHeight = minecraft.getWindow().getGuiScaledHeight();
    int wWidth = minecraft.getWindow().getGuiScaledWidth();

    this.navigationWidget = TabNavigationWidget.builder(this.tabManager, getViewBoxWidth())
        .tabs(this.categories.keySet().toArray(Component[]::new))
        .build();

    for (ConfigElementListWidget value : this.categories.values()) {
      value.dimensions(
          0, getHeaderSize(), getViewBoxWidth(), wHeight - getFooterSize() - getHeaderSize());
      value.rebuildPositions();
    }

    int backLabelWidth = minecraft.font.width(BACK_LABEL);
    this.backWidget = Button.builder(BACK_LABEL, button -> this.onClose())
        .pos(3, getViewBoxBottom() + 3)
        .size(backLabelWidth + 16, 20)
        .build();

    int saveLabelWidth = minecraft.font.width(SAVE_LABEL);
    this.saveWidget = Button.builder(SAVE_LABEL, button -> {
          if (!this.requiresRestart)
            this.requiresRestart =
                this.allChildren.stream().anyMatch(e -> e.requiresRestart() && e.modified());

          this.allChildren.forEach(AbstractConfigElement::save);
          this.saveFunction.run();
        })
        .pos(3 + backLabelWidth + 16 + 3, getViewBoxBottom() + 3)
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

    this.addRenderableWidget(this.navigationWidget);
    this.addRenderableWidget(this.currentCategory);
    this.addRenderableWidget(this.backWidget);
    this.addRenderableWidget(this.saveWidget);
    this.addRenderableWidget(this.optionInfoWidget);

    this.navigationWidget.selectTab(0, false);
    this.navigationWidget.init();
    this.updateWidgets();
  }

  private void updateWidgets() {
    this.saveWidget.active = this.edited && !this.erroring;
    this.saveWidget.setMessage(
        erroring ? SAVE_LABEL.copy().withStyle(ChatFormatting.RED) : SAVE_LABEL);
  }

  private final Deque<Runnable> renderTasks = new ArrayDeque<>();

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    this.renderBackground(context);

    context.fill(0, 0, this.width, getHeaderSize(), ColorUtil.toColor(0, 0, 0, 130));
    context.fill(0, getViewBoxBottom(), this.width, this.height, ColorUtil.toColor(0, 0, 0, 130));

    context.blit(
        CreateWorldScreen.FOOTER_SEPERATOR,
        0,
        Mth.roundToward(this.height - getFooterSize() - 2, 2),
        0.0F,
        0.0F,
        this.width,
        2,
        32,
        2);
    super.render(context, mouseX, mouseY, delta);
    context.blit(CreateWorldScreen.HEADER_SEPERATOR, 0, 24 - 2, 0.0F, 0.0F, this.width, 2, 32, 2);

    if (this.categories.size() <= 1)
      context.drawCenteredString(this.font, this.title, this.width / 2, 12 - 4, 16777215);

    if (this.erroring
        && saveWidget.visible
        && mouseX >= saveWidget.getX()
        && mouseX <= saveWidget.getX() + saveWidget.getWidth()
        && mouseY >= saveWidget.getY()
        && mouseY <= saveWidget.getY() + saveWidget.getHeight()) {
      context.renderComponentTooltip(
          minecraft.font, List.of(SAVE_ERROR_TITLE, SAVE_ERROR_DESC), mouseX, mouseY);
    }

    synchronized (this.renderTasks) {
      while (!this.renderTasks.isEmpty()) {
        Runnable runnable = this.renderTasks.poll();
        if (runnable != null) runnable.run();
      }
    }
  }

  public int getViewBoxWidth() {
    return (int) (minecraft.getWindow().getGuiScaledWidth() / 1.45);
  }

  public int getViewBoxTop() {
    return getHeaderSize();
  }

  public int getViewBoxBottom() {
    return minecraft.getWindow().getGuiScaledHeight() - getFooterSize();
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
  public void onClose() {
    MakeSure.notNull(minecraft);

    if (this.allChildren.stream().anyMatch(AbstractConfigElement::modified)) {
      this.minecraft.setScreen(new ConfirmScreen(
          response -> {
            if (response) {
              this.saveWidget.onPress();
              this.onClose(); // We have to check requiresRestart
            } else this.minecraft.setScreen(this.parent);
          },
          CHANGES_TITLE,
          CHANGES_MSG,
          CHANGES_YES,
          CHANGES_NO));
      return;
    }

    if (this.requiresRestart) {
      this.minecraft.setScreen(new ConfirmScreen(
          response -> {
            if (response) this.minecraft.stop();
            else this.minecraft.setScreen(this.parent);
          },
          RESTART_TITLE,
          RESTART_MSG,
          RESTART_YES,
          RESTART_NO));
      return;
    }
    this.minecraft.setScreen(this.parent);
  }

  public static void fillGradientHorizontal(
      PoseStack matrices,
      int startX,
      int startY,
      int endX,
      int endY,
      int colorStart,
      int colorEnd) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    Tesselator tesselator = Tesselator.getInstance();
    BufferBuilder bufferBuilder = tesselator.getBuilder();
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    fillGradientHorizontal(
        matrices.last().pose(), bufferBuilder, startX, startY, endX, endY, 0, colorStart, colorEnd);
    tesselator.end();
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

    builder.vertex(matrix, endX, startY, z).color(k, l, m, j).endVertex();
    builder.vertex(matrix, startX, startY, z).color(g, h, i, f).endVertex();
    builder.vertex(matrix, startX, endY, z).color(g, h, i, f).endVertex();
    builder.vertex(matrix, endX, endY, z).color(k, l, m, j).endVertex();
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
