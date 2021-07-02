package luckmerlin.core;

public class InvokeFinisher{

    public final <T> boolean onInvokeFinish(int code,String note,T data,OnInvokeFinish<T> callback){
        if (null!=callback){
            callback.onInvokeFinish(code,note,data);
            return true;
        }
        return false;
    }
}
