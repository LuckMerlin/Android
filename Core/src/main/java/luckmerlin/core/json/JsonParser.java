package luckmerlin.core.json;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Collection;
import luckmerlin.core.Convertor;

public  class JsonParser {

    public final <F,T> T object(Convertor<F,T> parser, F json){
        return null!=parser&&null!=json?parser.convert(json):null;
    }

    public final <T,M extends Collection<T>> M list(Convertor<Object,T> parser, Object json){
        return list(parser,json,null);
    }

    public final <T,M extends Collection<T>> M list(Convertor<Object,T> parser,Object json, M collection){
        JSONArray array=null!=json&&null!=parser?Json.createArray(json):null;
        if (null!=array){
            int length=array.length();
            collection=null!=collection?collection: (M) new ArrayList<T>(length);
            T child=null;
            for (int i = 0; i < length; i++) {
                if (null!=(child=parser.convert(array.opt(i)))){
                    collection.add(child);
                }
            }
            return collection;
        }
        return null;
    }
}
