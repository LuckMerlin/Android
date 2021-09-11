package luckmerlin.core.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MatchIterator {

    public final<T> List<T> iterate(T[] values, Matchable<T> matchable){
        int length=null!=values&&null!=matchable?values.length:0;
        if (length>0){
            int max=matchable.getMax();
            List<T> list=new ArrayList<>(length>=max?length:Math.max(0,max));
            synchronized (values) {
                for (T child : values) {
                    if (list.size() >= max) {
                        break;
                    }
                    if (null == child) {
                        continue;
                    } else if (null == matchable) {
                        list.add(child);
                        continue;
                    }
                    Integer integer = matchable.onMatch(child);
                    if (null == integer || integer == Matchable.CONTINUE) {
                        continue;
                    } else if (integer == Matchable.BREAK) {
                        break;
                    } else if (integer == Matchable.MATCHED) {
                        list.add(child);
                    }
                }
            }
            return list;
        }
        return null;
    }

    public final<T> List<T> iterate(Collection<T> values, Matchable<T> matchable){
        int length=null!=values&&null!=matchable?values.size():0;
        if (length>0){
            int max=matchable.getMax();
            List<T> list=new ArrayList<>(length>=max?max:length);
            synchronized (values){
                for (T child:values) {
                    if (list.size()>=max){
                        break;
                    }
                    if (null==child){
                        continue;
                    }else if (null==matchable){
                        list.add(child);
                        continue;
                    }
                    Integer integer= matchable.onMatch(child);
                    if (null==integer||integer== Matchable.CONTINUE){
                        continue;
                    }else if (integer== Matchable.BREAK){
                        break;
                    }else if (integer== Matchable.MATCHED){
                        list.add(child);
                    }
                }
            }
            return list;
        }
        return null;
    }

}
