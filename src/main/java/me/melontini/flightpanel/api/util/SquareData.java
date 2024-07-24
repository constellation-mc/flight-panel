package me.melontini.flightpanel.api.util;

import me.melontini.flightpanel.impl.widgets.ConfigElementListWidget;

public record SquareData(int x, int y, int endX, int endY, int width, int height) {

  public static SquareData of(ConfigElementListWidget widget) {
    return new SquareData(
        widget.x,
        widget.y,
        widget.x + widget.width,
        widget.y + widget.height,
        widget.width,
        widget.height);
  }

  public static SquareData of(int x, int y, int width, int height) {
    return new SquareData(x, y, x + width, y + height, width, height);
  }

  public SquareData withHeight(int height) {
    return of(x, y, width, height);
  }

  public SquareData withWidth(int width) {
    return of(x, y, width, height);
  }

  public boolean intersects(SquareData data) {
    return endY >= data.y && y <= data.endY && endX >= data.x && x <= data.endX;
  }

  public boolean isWithIn(SquareData data) {
    return y >= data.y && y <= data.endY && x >= data.x && x <= data.endX;
  }

  public boolean withinBounds(int x, int y) {
    return y >= this.y && y <= this.endY && x >= this.x && x <= this.endX;
  }
}
