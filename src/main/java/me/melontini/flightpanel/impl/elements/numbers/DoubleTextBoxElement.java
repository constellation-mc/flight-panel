package me.melontini.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Doubles;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import me.melontini.flightpanel.api.builders.elements.numbers.DoubleTextBoxBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberTextBoxElement;
import me.melontini.flightpanel.api.util.Result;
import net.minecraft.text.Text;

public class DoubleTextBoxElement extends AbstractNumberTextBoxElement<Double, DoubleTextBoxElement> {

    public DoubleTextBoxElement(DoubleTextBoxBuilder builder) {
        super(builder);
    }

    @Override
    protected Tuple<Double, Double> defaultRange() {
        return Tuple.of(-Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    protected boolean validChar(char c) {
        return c == '.' || c == 'E' || c == '-';
    }

    @Override
    protected String convertToString(Double obj) {
        return Double.toString(obj);
    }

    @Override
    protected Result<Double, Text> convertToNumber(String s) {
        var num = Doubles.tryParse(s);
        return num == null ? Result.error(Text.translatable("service.flight-panel.error.number.invalid_double")) : Result.success(num);
    }
}
