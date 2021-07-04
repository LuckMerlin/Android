package luckmerlin.core.media;

import luckmerlin.core.Canceler;
import luckmerlin.core.OnPageLoadFinish;

public interface Queue<T extends Media>  {
     Canceler load(final long from, int size, Object args, final OnPageLoadFinish<T> callback);
}
