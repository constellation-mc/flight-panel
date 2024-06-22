package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.api.util.DataType;
import me.melontini.flightpanel.impl.elements.numbers.DoubleSliderElement;
import net.minecraft.text.Text;

import java.math.RoundingMode;

public class DoubleSliderBuilder extends RangedNumberElementBuilder<Double, DoubleSliderElement, DoubleSliderBuilder> {

    public static final DataType<Integer> PLACES = DataType.of();
    public static final DataType<RoundingMode> ROUNDING_MODE = DataType.of();

    public static DoubleSliderBuilder create(Text elementName, double value, double min, double max) {
        return new DoubleSliderBuilder(elementName, value, min, max);
    }

    protected DoubleSliderBuilder(Text elementName, double value, double min, double max) {
        super(elementName, value);
        this.min(min);
        this.max(max);
    }

    public DoubleSliderBuilder places(int places) {
        return data(PLACES, places);
    }

    public DoubleSliderBuilder roundingMode(RoundingMode mode) {
        return data(ROUNDING_MODE, mode);
    }

    @Override
    public DoubleSliderElement build() {
        return new DoubleSliderElement(this);
    }
}
