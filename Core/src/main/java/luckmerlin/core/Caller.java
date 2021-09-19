package luckmerlin.core;

public class Caller<T> extends Reply<T>{
    private Canceler mCanceler;

    public Caller(int code, String note, T data) {
        super(code, note, data);
    }

    public final Caller setCanceler(Canceler canceler) {
        this.mCanceler = canceler;
        return this;
    }

    public Canceler getCanceler() {
        return mCanceler;
    }
}
