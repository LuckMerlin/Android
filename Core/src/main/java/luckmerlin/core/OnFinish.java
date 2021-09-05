package luckmerlin.core;

public interface OnFinish<T> {
    void onFinish(int code,String note,T data);

    default <T> boolean finish(int code, String note, T data, OnFinish<T> callback){
        if (null!=callback){
            callback.onFinish(code,note,data);
            return true;
        }
        return false;
    }
}
