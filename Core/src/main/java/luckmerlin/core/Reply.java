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
}
