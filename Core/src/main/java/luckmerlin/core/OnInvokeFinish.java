package luckmerlin.core;

public interface OnInvokeFinish<T> {
    void onInvokeFinish(int code,String note,T data);
}
