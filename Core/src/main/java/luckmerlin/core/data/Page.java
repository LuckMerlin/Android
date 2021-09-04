package luckmerlin.core.data;

import java.util.Collection;

public class Page<T>{
    private Collection<T> mData;
    private long mFrom;
    private long mTotal;

    public Page(){
        this(0);
    }

    public Page(long total){
        this(0,total);
    }

    public Page(long from,long total){
        this(from,total,null);
    }

    public Page(long from,long total,Collection<T> data){
        mFrom=from;
        mTotal=total;
        mData=data;
    }

    public final long getFrom() {
        return mFrom;
    }

    public final Collection<T> getData() {
        return mData;
    }

    public final long getTotal() {
        return mTotal;
    }

    public final Page setData(Collection<T> data) {
        this.mData = data;
        return this;
    }

    public final Page setTotal(long total) {
        this.mTotal = total;
        return this;
    }

    public final Page setFrom(long from) {
        this.mFrom = from;
        return this;
    }
}
