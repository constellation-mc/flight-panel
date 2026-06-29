package me.melontini.flightpanel.impl.util;

import java.util.Optional;
import net.minecraft.network.chat.Component;

public class TextUtil {

  public static boolean isEmpty(Component text) {
    return text.visit(Optional::ofNullable).filter(s -> !s.isEmpty()).isEmpty();
  }

  public static boolean isBlank(Component text) {
    return text.visit(Optional::ofNullable).filter(s -> !s.isBlank()).isEmpty();
  }
}
