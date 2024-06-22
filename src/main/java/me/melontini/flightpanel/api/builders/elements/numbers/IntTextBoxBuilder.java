package me.melontini.flightpanel.api.builders.elements.numbers;

import me.melontini.flightpanel.impl.elements.numbers.IntTextBoxElement;
import net.minecraft.text.Text;

public class IntTextBoxBuilder extends RangedNumberElementBuilder<Integer, IntTextBoxElement, IntTextBoxBuilder> {

    public static IntTextBoxBuilder create(Text elementName, int value) {
        return new IntTextBoxBuilder(elementName, value);
    }

    protected IntTextBoxBuilder(Text elementName, int value) {
        super(elementName, value);
    }

    @Override
    public IntTextBoxElement build() {
        return new IntTextBoxElement(this);
    }
}
