package me.melontini.flightpanel.api.util;

public class DataType<T> {

  public static <T> DataType<T> of() {
    return new DataType<>();
  }
}
