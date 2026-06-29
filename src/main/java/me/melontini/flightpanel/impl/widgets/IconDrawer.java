package me.melontini.flightpanel.impl.widgets;

import dev.zenfyr.pulsar.api.util.ColorUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Accessors(fluent = true)
@Getter
@Setter
@Builder
public class IconDrawer {

  private final ResourceLocation texture;
  private int x, y;
  private int width, height;
  private int textureWidth, textureHeight;
  private int u, v;
  private int color;

  public void renderIcon(GuiGraphics context, boolean shadow) {
    if (shadow) {
      context.setColor(
          ColorUtil.getRedF(color) * 0.25F,
          ColorUtil.getGreenF(color) * 0.25F,
          ColorUtil.getBlueF(color) * 0.25F,
          1);
      context.blit(texture, x + 1, y + 1, 0, u, v, width, height, textureWidth, textureHeight);
    }
    context.setColor(
        ColorUtil.getRedF(color), ColorUtil.getGreenF(color), ColorUtil.getBlueF(color), 1);
    context.blit(texture, x, y, 0, u, v, width, height, textureWidth, textureHeight);
    context.setColor(1, 1, 1, 1);
  }

  public static class IconDrawerBuilder {

    public IconDrawerBuilder uv(int u, int v) {
      return this.u(u).v(v);
    }

    public IconDrawerBuilder position(int x, int y) {
      return this.x(x).y(y);
    }

    public IconDrawerBuilder dimensions(int width, int height) {
      return this.width(width).height(height);
    }

    public IconDrawerBuilder textureDimensions(int width, int height) {
      return this.textureWidth(width).textureHeight(height);
    }
  }
}
