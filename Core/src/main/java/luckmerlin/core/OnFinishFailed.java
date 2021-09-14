package luckmerlin.core;

public interface OnFinishFailed<T> extends OnFinish<T>{

    void onFailed(int code,String note, T data);

    @Override
    default void onFinish(int code, String note, T data){
        if ((code&Code.CODE_SUCCEED)<=0){
            onFailed(code,note,data);
        }
    }

}
