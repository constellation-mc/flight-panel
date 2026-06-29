package me.melontini.flightpanel.api.elements;

import dev.zenfyr.pulsar.api.util.ColorUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.util.ConfigScreenProxy;
import me.melontini.flightpanel.api.util.SquareData;
import me.melontini.flightpanel.impl.ConfigScreen;
import me.melontini.flightpanel.impl.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
public abstract class AbstractConfigElement<T, S extends AbstractConfigElement<T, S>>
    extends AbstractContainerEventHandler implements NarratableEntry {

  private static final int HIGHLIGHT_START = ColorUtil.toColor(255, 255, 255, 0);
  private static final int HIGHLIGHT_END = ColorUtil.toColor(255, 255, 255, 90);

  @NotNull protected final Minecraft client = Minecraft.getInstance();

  @Getter
  private final Component elementName;

  @Getter
  private final boolean elementNameEmpty;

  private final boolean requiresRestart;
  private final @Nullable List<Component> elementDescription;

  @Getter
  protected SquareData pos;

  public AbstractConfigElement(BaseElementBuilder<T, S, ?> builder) {
    this.elementName = builder.dataOrThrow(BaseElementBuilder.ELEMENT_NAME);
    this.elementNameEmpty = TextUtil.isEmpty(this.elementName());

    this.requiresRestart = builder.dataOrElse(BaseElementBuilder.REQUIRES_RESTART, false);

    List<Component> generatedDesc = builder.data(BaseElementBuilder.DESCRIPTION);
    if (generatedDesc == null) {
      if (elementName.getContents() instanceof TranslatableContents ttc) {
        String key = (ttc.getKey().endsWith(".")
                ? ttc.getKey().substring(0, ttc.getKey().length() - 1)
                : ttc.getKey())
            + ".@Tooltip";
        if (I18n.exists(key))
          generatedDesc = Collections.singletonList(Component.translatable(key));
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

  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    if (!this.isMouseOver(mouseX, mouseY)) {
      int relMouseX = Mth.clamp(mouseX, pos.x(), pos.endX());

      firstHighlightTarget = relMouseX;
      secondHighlightTarget = relMouseX;
    } else {
      this.renderMouseHover(context, mouseX);
      var errorTooltip = this.getElementError();
      if (errorTooltip != null)
        this.proxy()
            .queuePostElementRender(
                () -> this.renderErrorTooltip(context, mouseX, mouseY, errorTooltip));
    }
  }

  public void renderMouseHover(GuiGraphics context, int mouseX) {
    int relMouseX = Mth.clamp(mouseX, pos.x(), pos.endX());

    this.firstHighlightTarget =
        Mth.lerpInt(0.5f * client.getDeltaFrameTime(), this.firstHighlightTarget, this.pos.x());
    this.secondHighlightTarget =
        Mth.lerpInt(0.5f * client.getDeltaFrameTime(), this.secondHighlightTarget, this.pos.endX());

    ConfigScreen.fillGradientHorizontal(
        context.pose(),
        this.firstHighlightTarget,
        this.pos.y(),
        relMouseX,
        this.pos.y() + 1,
        HIGHLIGHT_START,
        HIGHLIGHT_END);
    ConfigScreen.fillGradientHorizontal(
        context.pose(),
        relMouseX,
        this.pos.y(),
        this.secondHighlightTarget,
        this.pos.y() + 1,
        HIGHLIGHT_END,
        HIGHLIGHT_START);

    ConfigScreen.fillGradientHorizontal(
        context.pose(),
        this.firstHighlightTarget,
        this.pos.endY() - 1,
        relMouseX,
        this.pos.endY(),
        HIGHLIGHT_START,
        HIGHLIGHT_END);
    ConfigScreen.fillGradientHorizontal(
        context.pose(),
        relMouseX,
        this.pos.endY() - 1,
        this.secondHighlightTarget,
        this.pos.endY(),
        HIGHLIGHT_END,
        HIGHLIGHT_START);
  }

  public void renderErrorTooltip(
      GuiGraphics context, int mouseX, int mouseY, Component errorTooltip) {
    context.renderTooltip(
        client.font, errorTooltip.copy().withStyle(ChatFormatting.RED), mouseX, mouseY);
  }

  public void tick() {}

  public void save() {}

  public S self() {
    return (S) this;
  }

  public boolean requiresRestart() {
    return requiresRestart;
  }

  public Component displayName(int mouseX, int mouseY) {
    MutableComponent text = elementName().copy().withStyle(ChatFormatting.GRAY);
    if (this.isMouseOver(mouseX, mouseY)) text.withStyle(ChatFormatting.WHITE);
    if (modified()) text.withStyle(ChatFormatting.ITALIC);
    if (getElementError() != null) {
      if (this.elementNameEmpty)
        text.append(Component.translatable("service.flight-panel.error.widget.generic"));
      text.withStyle(ChatFormatting.RED);
    }
    return text;
  }

  public Collection<Component> description() {
    return this.elementDescription != null ? this.elementDescription : Collections.emptyList();
  }

  public @Nullable Component getElementError() {
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
    return pos.withinBounds((int) mouseX, (int) mouseY)
        && this.proxy().isPointWithinListBounds(mouseX, mouseY);
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

  // TODO I have no idea how to make narrations work proper.
  @Override
  public NarrationPriority narrationPriority() {
    return NarrationPriority.NONE;
  }

  @Override
  public void updateNarration(NarrationElementOutput output) {
    output.add(NarratedElementType.TITLE, elementName());
  }

  public @Nullable AbstractConfigElement<?, ?> hoveredChildOrSelf(int mouseX, int mouseY) {
    return isMouseOver(mouseX, mouseY) ? this : null;
  }

  @ApiStatus.Internal
  protected ConfigScreenProxy proxy() {
    if (Minecraft.getInstance().screen instanceof ConfigScreenProxy csp) return csp;
    throw new IllegalStateException();
  }
}
