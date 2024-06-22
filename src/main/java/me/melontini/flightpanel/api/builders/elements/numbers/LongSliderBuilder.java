package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.LongSliderElement;
import net.minecraft.text.Text;

public class LongSliderBuilder extends RangedNumberElementBuilder<Long, LongSliderElement, LongSliderBuilder> {

    public static LongSliderBuilder create(Text elementName, long value, long min, long max) {
        return new LongSliderBuilder(elementName, value, min, max);
    }

    protected LongSliderBuilder(Text elementName, long value, long min, long max) {
        super(elementName, value);
        this.min(min);
        this.max(max);
    }

    @Override
    public LongSliderElement build() {
        return new LongSliderElement(this);
    }
}
