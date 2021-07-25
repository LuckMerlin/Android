package luckmerlin.database.android;

import android.content.Context;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.os.CancellationSignal;
import java.io.IOException;
import luckmerlin.database.AbstractDatabase;
import luckmerlin.database.Cursor;
import luckmerlin.database.TableValues;

public class AndroidDatabase extends AbstractDatabase {
    private final SQLiteDatabase mDatabase;

    public AndroidDatabase(Context context,String dbName){
        mDatabase=context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
    }

    public AndroidDatabase(SQLiteDatabase database){
        mDatabase=database;
    }

    @Override
    public int execSQL(String sql,Object[] bindArgs) {
        SQLiteDatabase database=mDatabase;
//        database.execSQL();
//        return null!=database?database.execSQL(sql,bindArgs):-1;
        return -1;
    }


    @Override
    public long insert(String table, String nullColumnHack, TableValues values) {
        return 0;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
        return null;
    }

    @Override
    public int update(String table, TableValues values, String whereClause, String[] whereArgs) {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
