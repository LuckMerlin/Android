package luckmerlin.core;

public class Reply<T> implements Result{
    private int mCode;
    private String mNote;
    private T mData;

    public Reply(int code){
        this(code,null);
    }

    public Reply(int code,String note){
        this(code,note,null);
    }

    public Reply(int code,String note,T data){
        mCode=code;
        mNote=note;
        mData=data;
    }

    public final boolean isSucceed(){
        return Code.isSucceed(getCode());
    }

    public final int getCode() {
        return mCode;
    }

    public final String getNote() {
        return mNote;
    }

    public final T getData() {
        return mData;
    }
}
