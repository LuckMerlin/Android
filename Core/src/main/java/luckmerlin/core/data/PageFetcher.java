package luckmerlin.core.data;

import java.util.ArrayList;
import java.util.List;
import luckmerlin.core.Convertor;
import luckmerlin.core.Indexer;
import luckmerlin.core.debug.Debug;

public final class PageFetcher {

    public final <A,T> Page<T> fromList(A anchor, int limit, List<A> collection,
                                        Indexer<A> indexer, Convertor<A, T> convertor){
        if (null==convertor){
            return null;
        }
        int length=null!=collection?collection.size():0;
        final Page<T> page=new Page<>().setTotal(length);
        if (length<=0){
            return page;
        }
        int anchorIndex=limit>0?0:length-1;
        final int shift=limit>0?1:-1;
        if (null!=anchor) {
            if ((anchorIndex=(null==indexer?new Indexer<A>(){
                @Override
                public int index(A data) {
                    return null!=collection&&null!=data?collection.indexOf(data):-1;
                }
            }:indexer).index(anchor))>=0&&anchorIndex<length){
                anchorIndex+=shift;//Make shift to ignore anchor
            }
        }
        if (anchorIndex<0||anchorIndex>=length){
            Debug.W("Can't fetch page while anchor index invalid."+anchorIndex+" "+length+" "+anchor);
            return null;
        }
        int size=Math.abs(limit);
        final List<T> list=new ArrayList<>();
        A child=null;T childPath=null;
        page.setFrom(anchorIndex);
        for (;anchorIndex < length&&anchorIndex>=0&&size>0; anchorIndex+=shift) {
            if (null==(child=collection.get(anchorIndex))||null==(childPath=convertor.convert(child))){
                Debug.D("Skip fetch page one which create invalid."+child);
                continue;
            }else if (shift>0){
                list.add(childPath);
            }else{
                list.add(0,childPath);
            }
            --size;
        }
        return page.setData(list);
    }
}
