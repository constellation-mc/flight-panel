package me.melontini.flightpanel.impl.util;

import me.melontini.flightpanel.api.elements.AbstractConfigElement;

public interface ConfigScreenProxy {

    void setDisplayEntry(AbstractConfigElement<?, ?> display);
    void rebuildPositions();
}
