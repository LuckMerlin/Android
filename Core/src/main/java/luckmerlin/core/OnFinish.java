package luckmerlin.core;

public interface OnFinish<T> {
    void onFinish(int code,String note,T data);
}
