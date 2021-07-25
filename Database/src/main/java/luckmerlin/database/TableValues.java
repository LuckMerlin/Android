package luckmerlin.database;

import org.json.JSONArray;
import org.json.JSONObject;

import luckmerlin.core.debug.Debug;

public final class TableValues {
    private final android.content.ContentValues mContentValues=new android.content.ContentValues();

    public TableValues putSafeWithNull(String key, Object value) {
        return putSafe(key, value,true);
    }

    public TableValues putSafe(String key, Object value, boolean putNull) {
        if (null != key) {
            android.content.ContentValues values = mContentValues;
            if (value ==null){
                if (putNull){
                    values.putNull(key);
                }
                return this;
            }else if (value instanceof Integer) {
                values.put(key, (Integer) value);
                return this;
            } else if (value instanceof String) {
                values.put(key, (String) value);
                return this;
            } else if (value instanceof Double) {
                values.put(key, (Double) value);
                return this;
            } else if (value instanceof Byte) {
                values.put(key, (Byte) value);
                return this;
            } else if (value instanceof Long) {
                values.put(key, (Long) value);
                return this;
            } else if (value instanceof Float) {
                values.put(key, (Float) value);
                return this;
            } else if (value instanceof Short) {
                values.put(key, (Short) value);
                return this;
            } else if (value instanceof Boolean) {
                values.put(key, (Boolean) value);
                return this;
            } else if (value instanceof byte[]) {
                values.put(key, (byte[]) value);
                return this;
            }else if (value instanceof JSONObject||value instanceof JSONArray){
                return putSafe(key, value.toString(),putNull);
            }
            Debug.TW("Fail put DB content values which NOT support.", (null!=value?value.getClass():"")+" "+value);
            return this;
        }
        Debug.W("Fail put DB content values which key Invalid.");
        return this;
    }

    public int size(){
        return mContentValues.size();
    }

    public android.content.ContentValues getContentValues() {
        return mContentValues;
    }
}
