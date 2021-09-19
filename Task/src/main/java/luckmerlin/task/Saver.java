package luckmerlin.task;

public interface Saver {
    Task create(Saved saved);
    boolean save(Saved saved);
    boolean delete(Saved saved);
}
