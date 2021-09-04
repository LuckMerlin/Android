package luckmerlin.core.json;

public interface Parser<T> {
    T parse(int index,Object json);
}
