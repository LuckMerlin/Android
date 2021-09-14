package luckmerlin.core;

public interface OnFinishSucceed<T> extends OnFinish<T>{

    void onSucceed(int code,String note, T data);

    @Override
    default void onFinish(int code, String note, T data){
        if ((code&Code.CODE_SUCCEED)>0){
            onSucceed(code,note,data);
        }
    }

}
