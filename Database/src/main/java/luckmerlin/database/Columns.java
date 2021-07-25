package luckmerlin.database;

import org.json.JSONObject;

import java.util.Iterator;

import luckmerlin.core.json.Json;

public class Columns extends Json {

    public Columns(){
        this(null);
    }

    public Columns(JSONObject jsonObject){
        super(jsonObject);
    }

    public final Columns max(String column,String asName){
        return method("max",column,asName);
    }

    public final Columns min(String column,String asName){
        return method("min",column,asName);
    }

    public final Columns last(String column,String asName){
        return method("last",column,asName);
    }

    public final Columns first(String column,String asName){
        return method("first",column,asName);
    }

    public final Columns count(String column,String asName){
        return method("count",column,asName);
    }

    public final Columns avg(String column,String asName){
        return method("avg",column,asName);
    }

    public final Columns sum(String column,String asName){
        return method("sum",column,asName);
    }

    final Columns method(String methodName,String column,String asName){
        methodName=null!=methodName?methodName.trim():null;
        if (null!=methodName&&methodName.length()>0&&
                null!=(column=null!=column?column.trim():null)&&column.length()>0){
            return putJsonValueSafe(this,methodName+"("+column+")",null!=asName?asName.trim():null);
        }
        return this;
    }

    public final String columns(){
        synchronized (this){
            int length=length();
            StringBuffer buffer=new StringBuffer(length+1);
            Iterator<String> iterator=keys();
            if (null!=iterator&&iterator.hasNext()){
                String child=null;String value=null;
                do {
                    if (null!=(child=iterator.next())&&null!=(child=child.trim())&&child.length()>0){
                        buffer.append(buffer.length()>0?" ,"+child:child);
                        if (null!=(value=optString(child,null))&&null!=(value=value.trim())&&value.length()>0){
                            buffer.append(" as "+value);
                        }
                    }
                }while (iterator.hasNext());
            }
            return buffer.toString();
        }
    }
}
