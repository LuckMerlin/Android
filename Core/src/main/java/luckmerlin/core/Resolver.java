package luckmerlin.core;

public interface Resolver<T> {
    boolean onResolve(T data);
}
