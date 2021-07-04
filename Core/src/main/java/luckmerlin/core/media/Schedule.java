package luckmerlin.core.media;

public interface Schedule {
    Integer mode(Integer mode, String debug);
    Integer getCurrent();
    int pre(int index, int size);
    int next(int index, int size, boolean user);
}
