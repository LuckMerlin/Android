package luckmerlin.database.android;

import java.io.IOException;
import luckmerlin.core.io.Closer;
import luckmerlin.database.Cursor;

final class AndroidCursor implements Cursor,Closer{
    private final android.database.Cursor mCursor;

    public AndroidCursor(android.database.Cursor cursor){
        mCursor=cursor;
    }

    @Override
    public void close() throws IOException {
        close(true,mCursor);
    }

    public final String getString(String column, String def){
        android.database.Cursor cursor=mCursor;
        int index=null!=cursor&&null!=column&&column.length()>0?cursor.getColumnIndex(column):-1;
        return index>=0?cursor.getString(index):def;
    }

    @Override
    public long getCount() {
        android.database.Cursor cursor=mCursor;
        return null!=cursor?cursor.getCount():-1;
    }

    @Override
    public boolean moveToFirst() {
        android.database.Cursor cursor=mCursor;
        return null!=cursor&&cursor.moveToFirst();
    }

    @Override
    public boolean moveToNext() {
        android.database.Cursor cursor=mCursor;
        return null!=cursor&&cursor.moveToNext();
    }

    public final long getLong(String column, long def){
        android.database.Cursor cursor=mCursor;
        int index=null!=cursor&&null!=column&&column.length()>0?cursor.getColumnIndex(column):-1;
        return index>=0?cursor.getLong(index):def;
    }

    public final float getFloat(String column, float def){
        android.database.Cursor cursor=mCursor;
        int index=null!=cursor&&null!=column&&column.length()>0?cursor.getColumnIndex(column):-1;
        return index>=0?cursor.getFloat(index):def;
    }

    public final int getInt(String column, int def){
        android.database.Cursor cursor=mCursor;
        int index=null!=cursor&&null!=column&&column.length()>0?cursor.getColumnIndex(column):-1;
        return index>=0?cursor.getInt(index):def;
    }

    public final short getShort(String column, short def){
        android.database.Cursor cursor=mCursor;
        int index=null!=cursor&&null!=column&&column.length()>0?cursor.getColumnIndex(column):-1;
        return index>=0?cursor.getShort(index):def;
    }

    public final double getDouble(String column, double def){
        android.database.Cursor cursor=mCursor;
        int index=null!=cursor&&null!=column&&column.length()>0?cursor.getColumnIndex(column):-1;
        return index>=0?cursor.getDouble(index):def;
    }

    public final byte[] getBlob(String column, byte[] def){
        android.database.Cursor cursor=mCursor;
        int index=null!=cursor&&null!=column&&column.length()>0?cursor.getColumnIndex(column):-1;
        return index>=0?cursor.getBlob(index):def;
    }
}
