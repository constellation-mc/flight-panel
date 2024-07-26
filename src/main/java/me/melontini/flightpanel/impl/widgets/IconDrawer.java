package me.melontini.flightpanel.impl.widgets;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.melontini.dark_matter.api.base.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Accessors(fluent = true)
@Getter
@Setter
@Builder
public class IconDrawer {

  private final Identifier texture;
  private int x, y;
  private int width, height;
  private int textureWidth, textureHeight;
  private int u, v;
  private int color;

  public void renderIcon(DrawContext context, boolean shadow) {
    if (shadow) {
      context.setShaderColor(
          ColorUtil.getRedF(color) * 0.25F,
          ColorUtil.getGreenF(color) * 0.25F,
          ColorUtil.getBlueF(color) * 0.25F,
          1);
      context.drawTexture(
          texture, x + 1, y + 1, 0, u, v, width, height, textureWidth, textureHeight);
    }
    context.setShaderColor(
        ColorUtil.getRedF(color), ColorUtil.getGreenF(color), ColorUtil.getBlueF(color), 1);
    context.drawTexture(texture, x, y, 0, u, v, width, height, textureWidth, textureHeight);
    context.setShaderColor(1, 1, 1, 1);
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
