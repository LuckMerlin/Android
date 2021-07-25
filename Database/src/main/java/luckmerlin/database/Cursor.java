package luckmerlin.database;

import org.json.JSONObject;
import java.io.Closeable;

import luckmerlin.core.json.Json;

public interface Cursor extends Closeable {
    boolean moveToFirst();
    boolean moveToNext();
    long getCount();
    String getString(String columnName,String def);
    long getLong(String columnName,long def);
    float getFloat(String columnName,float def);
    int getInt(String columnName,int def);
    short getShort(String column, short def);
    double getDouble(String column, double def);
    byte[] getBlob(String column, byte[] def);
    default JSONObject getJson(String columnName, JSONObject def){
        String columnValues=getString(columnName,null);
        JSONObject jsonObject= null!=columnValues&&columnValues.length()>0? Json.create(columnValues):def;
        return null!=jsonObject?jsonObject:def;
    }
}
