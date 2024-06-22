package me.melontini.flightpanel.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record Result<V, E>(Optional<V> value, Optional<E> error) {

    public static <V, E> Result<V, E> error(@NotNull E error) {
        return new Result<>(Optional.empty(), Optional.of(error));
    }

    public static <V, E> Result<V, E> success(@Nullable V value) {
        return new Result<>(Optional.ofNullable(value), Optional.empty());
    }
}
