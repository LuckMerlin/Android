package luckmerlin.core;

public interface Convertor<F,T> {
    T convert(F from);
}
