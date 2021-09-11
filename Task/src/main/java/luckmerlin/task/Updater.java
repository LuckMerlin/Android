package luckmerlin.task;

public abstract class Updater {
    abstract Updater update(int status, Task task,Progress arg);
    abstract Updater finisher(boolean add, Finisher runnable);
}
