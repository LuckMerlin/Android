package luckmerlin.core.data;

import luckmerlin.core.Canceler;

public interface Pager<A,T> {
    Canceler onLoad(A args,T anchor,int limit,OnPageLoadFinish<T> callback);
}
