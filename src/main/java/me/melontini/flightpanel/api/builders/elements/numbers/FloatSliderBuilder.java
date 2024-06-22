package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.FloatSliderElement;
import net.minecraft.text.Text;

import java.math.RoundingMode;

public class FloatSliderBuilder extends RangedNumberElementBuilder<Float, FloatSliderElement, FloatSliderBuilder> {

    public static FloatSliderBuilder create(Text elementName, float value, float min, float max) {
        return new FloatSliderBuilder(elementName, value, min, max);
    }

    protected FloatSliderBuilder(Text elementName, float value, float min, float max) {
        super(elementName, value);
        this.min(min);
        this.max(max);
    }

    public FloatSliderBuilder places(int places) {
        return data(DoubleSliderBuilder.PLACES, places);
    }

    public FloatSliderBuilder roundingMode(RoundingMode mode) {
        return data(DoubleSliderBuilder.ROUNDING_MODE, mode);
    }

    @Override
    public FloatSliderElement build() {
        return new FloatSliderElement(this);
    }
}
