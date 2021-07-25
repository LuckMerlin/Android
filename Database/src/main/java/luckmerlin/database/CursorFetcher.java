package luckmerlin.database;

import org.json.JSONObject;

class CursorFetcher {

    public final String getString(Cursor cursor, String column, String def){
        return null!=cursor?cursor.getString(column,def):def;
    }

    public final long getLong(Cursor cursor,String column,long def){
        return null!=cursor?cursor.getLong(column,def):def;
    }

    public final float getFloat(Cursor cursor,String column,float def){
        return null!=cursor?cursor.getFloat(column,def):def;
    }

    public final int getInt(Cursor cursor,String column,int def){
        return null!=cursor?cursor.getInt(column,def):def;
    }

    public final JSONObject getJson(Cursor cursor,String column,JSONObject def){
        return null!=cursor?cursor.getJson(column,def):def;
    }

     public final byte[] getJson(Cursor cursor,String column,byte[] def){
         return null!=cursor?cursor.getBlob(column,def):def;
     }

     public final short getShort(Cursor cursor,String column,short def){
         return null!=cursor?cursor.getShort(column,def):def;
     }
}
