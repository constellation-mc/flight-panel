package me.melontini.flightpanel.api.elements;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.melontini.flightpanel.api.builders.elements.numbers.RangedNumberElementBuilder;

@Accessors(fluent = true)
public abstract class AbstractNumberSliderElement<
        T extends Number, S extends AbstractNumberSliderElement<T, S>>
    extends AbstractSliderElement<T, S> {

  @Getter
  private final T min, max;

  public AbstractNumberSliderElement(RangedNumberElementBuilder<T, S, ?> builder) {
    super(builder);
    this.min = builder.dataOrThrow(builder.minType());
    this.max = builder.dataOrThrow(builder.maxType());
  }
}
