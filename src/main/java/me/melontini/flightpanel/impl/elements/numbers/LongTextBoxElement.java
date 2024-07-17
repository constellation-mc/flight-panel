package me.melontini.flightpanel.impl.elements.numbers;

import com.google.common.primitives.Longs;
import me.melontini.dark_matter.api.base.util.Result;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import me.melontini.flightpanel.api.builders.elements.numbers.LongTextBoxBuilder;
import me.melontini.flightpanel.api.elements.AbstractNumberTextBoxElement;
import net.minecraft.text.Text;

public class LongTextBoxElement extends AbstractNumberTextBoxElement<Long, LongTextBoxElement> {

    public LongTextBoxElement(LongTextBoxBuilder builder) {
        super(builder);
    }

    @Override
    protected Tuple<Long, Long> defaultRange() {
        return Tuple.of(-Long.MAX_VALUE, Long.MAX_VALUE);
    }

    @Override
    protected boolean validChar(char c) {
        return false;
    }

    @Override
    protected String convertToString(Long obj) {
        return Long.toString(obj);
    }

    @Override
    protected Result<Long, Text> convertToNumber(String s) {
        var num = Longs.tryParse(s);
        return num == null ? Result.error(Text.translatable("service.flight-panel.error.number.invalid_long")) : Result.ok(num);
    }
}
