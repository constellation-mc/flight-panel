package dev.zenfyr.flightpanel.api.util;

import dev.zenfyr.flightpanel.api.elements.AbstractConfigElement;

public interface ConfigScreenProxy {

  void setDisplayEntry(AbstractConfigElement<?, ?> display);

  void rebuildPositions();

  // We have to manually fix hover. Maybe I'll find a better solution in the future, but idk.
  boolean isPointWithinListBounds(double x, double y);

  void queuePostElementRender(Runnable runnable);
}
