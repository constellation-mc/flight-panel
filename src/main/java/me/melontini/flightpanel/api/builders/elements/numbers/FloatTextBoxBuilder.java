package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.FloatTextBoxElement;
import net.minecraft.text.Text;

public class FloatTextBoxBuilder extends RangedNumberElementBuilder<Float, FloatTextBoxElement, FloatTextBoxBuilder> {

    public static FloatTextBoxBuilder create(Text elementName, float value) {
        return new FloatTextBoxBuilder(elementName, value);
    }

    protected FloatTextBoxBuilder(Text elementName, float value) {
        super(elementName, value);
    }

    @Override
    public FloatTextBoxElement build() {
        return new FloatTextBoxElement(this);
    }
}
