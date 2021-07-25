package luckmerlin.database;

import java.util.HashMap;
import java.util.Set;

import luckmerlin.core.debug.Debug;

public class ColumnMap extends HashMap<String,String> {

    public final String columns(String divider){
        synchronized (this){
            Set<String> set=keySet();
            StringBuffer buffer=null;
            if (null!=set){
                buffer=new StringBuffer(size());
                for (String child:set) {
                    child=null!=child?child.trim():null;
                    if (null==child||child.length()<=0){
                         continue;
                    }
                    buffer.append((buffer.length()>0&&null!=divider?divider+child:child)+" "+get(child));
                }
            }
            return null!=buffer?buffer.toString():null;
        }
    }

    public final ColumnMap addLong(String ...columnNames){
        return add(long.class,null,columnNames);
    }

    public final ColumnMap addInteger(String ...columnNames){
        return add(int.class,null,columnNames);
    }

    public final ColumnMap addDouble(String ...columnNames){
        return add(double.class,null,columnNames);
    }

    public final ColumnMap addBoolean(String ...columnNames){
        return add(short.class,null,columnNames);
    }

    public final ColumnMap addShortInteger(String ...columnNames){
        return add(short.class,null,columnNames);
    }

    public final ColumnMap addText(String ...columnNames){
        return add(String.class,null,columnNames);
    }

    protected String onResolveColumnTypeTitle(Class cls,String arg){
        //Do nothing
        return null;
    }

    public final ColumnMap add(Class columnType, String arg,String ...columnNames){
        if (null!=columnNames&&columnNames.length>0&&null!=columnType){
            synchronized (this){
            for (String child:columnNames) {
                if (null==child||child.length()<=0){
                    Debug.W("Fail add column into map which's name INVALID."+child+" "+columnType);
                    continue;
                }
                String columnTypeTitle=onResolveColumnTypeTitle(columnType,arg);
                if (null==columnTypeTitle||columnTypeTitle.length()<=0){
                    if (isAnyClassEquals(columnType,long.class,Long.class)){
                        columnTypeTitle="integer";
                    }else if (isAnyClassEquals(columnType,int.class,Integer.class)){
                        columnTypeTitle="integer";
                    }else if (isAnyClassEquals(columnType,short.class,Short.class)){
                        columnTypeTitle="smallint";
                    }else if (isAnyClassEquals(columnType,float.class,Float.class)){
                        columnTypeTitle="float";
                    }else if (isAnyClassEquals(columnType,double.class,Double.class)){
                        columnTypeTitle="double";
                    }else if (isAnyClassEquals(columnType,String.class)){
                        columnTypeTitle="text";
                    }
                }
                if (null==columnTypeTitle||columnTypeTitle.length()<=0){
                    Debug.W("Fail add column into map which is not support."+child+" "+columnType);
                    return this;
                }
                    put(child,null!=arg&&arg.length()>0?columnTypeTitle+" "+arg+" ":columnTypeTitle);
                }
            }
        }
        return this;
    }

    private boolean isAnyClassEquals(Class target,Class...classes){
        if (null!=target&&null!=classes&&classes.length>0){
            String clsName=target.getName();
            for (Class child:classes) {
                  String childName=null!=child?child.getName():null;
                  if (null!=childName&&null!=clsName&&childName.equals(clsName)){
                      return true;
                  }
            }
        }
        return false;
    }
}
