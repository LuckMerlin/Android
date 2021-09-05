package luckmerlin.core;

public class Reply<T> {
    private int mCode;
    private String mNote;
    private T mData;

    public Reply(int code,String note,T data){
        mCode=code;
        mNote=note;
        mData=data;
    }

    public final boolean isSucceed(){
        return getCode()==Code.CODE_SUCCEED;
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
