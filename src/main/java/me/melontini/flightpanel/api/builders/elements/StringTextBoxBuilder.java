package me.melontini.flightpanel.api.builders.elements;

import me.melontini.flightpanel.api.util.DataType;
import me.melontini.flightpanel.impl.elements.StringTextBoxElement;
import net.minecraft.text.Text;

import java.util.function.Function;

public class StringTextBoxBuilder extends ValuedElementBuilder<String, StringTextBoxElement, StringTextBoxBuilder> {

    public static final DataType<Function<String, String>> SANITIZER = DataType.of();

    public static StringTextBoxBuilder create(Text elementName, String value) {
        return new StringTextBoxBuilder(elementName, value);
    }

    protected StringTextBoxBuilder(Text elementName, String value) {
        super(elementName, value);
    }

    public StringTextBoxBuilder sanitizer(Function<String, String> sanitizer) {
        return data(SANITIZER, sanitizer);
    }

    @Override
    public StringTextBoxElement build() {
        return new StringTextBoxElement(this);
    }
}
