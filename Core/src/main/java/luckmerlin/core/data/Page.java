package luckmerlin.core.data;

import java.util.List;

public class Page<T>{
    private final List<T> mData;
    private final long mFrom;
    private final long mTotal;

    public Page(long from, List<T> data){
        this(from,data,-1);
    }

    public Page(long from, List<T> data, long total){
        mFrom=from;
        mData=data;
        mTotal=total;
    }

    public final long getFrom() {
        return mFrom;
    }

    public final List<T> getData() {
        return mData;
    }

    public final long getTotal() {
        return mTotal;
    }
}
