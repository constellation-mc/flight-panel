package me.melontini.flightpanel.impl.elements;

import me.melontini.flightpanel.api.builders.elements.StringTextBoxBuilder;
import me.melontini.flightpanel.api.elements.AbstractTextBoxElement;
import me.melontini.flightpanel.api.util.Result;
import net.minecraft.text.Text;

import java.util.function.Function;

public class StringTextBoxElement extends AbstractTextBoxElement<String, StringTextBoxElement> {

    private final Function<String, String> sanitizer;

    public StringTextBoxElement(StringTextBoxBuilder builder) {
        super(builder);
        this.sanitizer = builder.dataOrElse(StringTextBoxBuilder.SANITIZER, Function.identity());
    }

    @Override
    protected String convertToString(String obj) {
        return obj;
    }

    @Override
    protected Result<String, Text> convertFromString(String s) {
        return Result.success(s);
    }

    @Override
    protected String sanitizeString(String s) {
        return this.sanitizer.apply(s);
    }
}
