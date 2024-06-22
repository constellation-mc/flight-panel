package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.DoubleTextBoxElement;
import net.minecraft.text.Text;

public class DoubleTextBoxBuilder extends RangedNumberElementBuilder<Double, DoubleTextBoxElement, DoubleTextBoxBuilder> {

    public static DoubleTextBoxBuilder create(Text elementName, double value) {
        return new DoubleTextBoxBuilder(elementName, value);
    }

    protected DoubleTextBoxBuilder(Text elementName, double value) {
        super(elementName, value);
    }

    @Override
    public DoubleTextBoxElement build() {
        return new DoubleTextBoxElement(this);
    }
}
