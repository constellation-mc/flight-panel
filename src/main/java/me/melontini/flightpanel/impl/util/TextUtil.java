package me.melontini.flightpanel.impl.util;

import java.util.Optional;
import net.minecraft.text.Text;

public class TextUtil {

  public static boolean isEmpty(Text text) {
    return text.visit(Optional::ofNullable).filter(s -> !s.isEmpty()).isEmpty();
  }

  public static boolean isBlank(Text text) {
    return text.visit(Optional::ofNullable).filter(s -> !s.isBlank()).isEmpty();
  }
}
