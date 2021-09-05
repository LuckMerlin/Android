package luckmerlin.core;

public class Call<T> extends Reply<T>{
    private Canceler mCanceler;

    public Call(int code, String note, T data) {
        super(code, note, data);
    }

    public final Call setCanceler(Canceler canceler) {
        this.mCanceler = canceler;
        return this;
    }

    public Canceler getCanceler() {
        return mCanceler;
    }
}
