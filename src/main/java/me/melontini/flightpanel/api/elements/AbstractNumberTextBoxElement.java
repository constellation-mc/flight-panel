package me.melontini.flightpanel.api.elements;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.melontini.dark_matter.api.base.util.tuple.Tuple;
import me.melontini.flightpanel.api.builders.elements.numbers.RangedNumberElementBuilder;
import me.melontini.flightpanel.api.util.Result;
import net.minecraft.text.Text;

@Accessors(fluent = true)
public abstract class AbstractNumberTextBoxElement<T extends Number & Comparable<T>, S extends AbstractNumberTextBoxElement<T, S>> extends AbstractTextBoxElement<T, S> {

    @Getter
    private final T min, max;

    public AbstractNumberTextBoxElement(RangedNumberElementBuilder<T, S, ?> builder) {
        super(builder);
        this.min = builder.dataOrElse(builder.minType(), defaultRange().left());
        this.max = builder.dataOrElse(builder.maxType(), defaultRange().right());
    }

    protected abstract Tuple<T, T> defaultRange();
    protected abstract boolean validChar(char c);

    @Override
    protected final Result<T, Text> convertFromString(String s) {
        Result<T, Text> result = convertToNumber(s);
        if (result.error().isPresent()) return result;
        T num = result.value().orElseThrow(IllegalStateException::new);

        if (num.compareTo(max) > 0) return Result.error(Text.translatable("service.flight-panel.error.number.max", max()));
        if (num.compareTo(min) < 0) return Result.error(Text.translatable("service.flight-panel.error.number.min", min()));
        return Result.success(num);
    }

    protected abstract Result<T, Text> convertToNumber(String s);

    @Override
    protected String sanitizeString(String s) {
        StringBuilder builder = new StringBuilder();
        char[] chars = s.toCharArray();
        for (char c : chars) if (Character.isDigit(c) || validChar(c)) builder.append(c);
        return builder.toString();
    }
}
