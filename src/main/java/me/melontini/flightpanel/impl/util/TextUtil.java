package me.melontini.flightpanel.impl.util;

import net.minecraft.text.Text;

import java.util.Optional;

public class TextUtil {

    public static boolean isEmpty(Text text) {
        return text.visit(Optional::ofNullable).filter(s -> !s.isEmpty()).isEmpty();
    }

    public static boolean isBlank(Text text) {
        return text.visit(Optional::ofNullable).filter(s -> !s.isBlank()).isEmpty();
    }
}
