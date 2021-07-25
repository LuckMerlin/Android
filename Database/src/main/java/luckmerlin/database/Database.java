package luckmerlin.database;

import android.os.CancellationSignal;
import java.io.Closeable;

public interface Database extends Closeable {
    int execSQL(String sql,Object[] bindArgs);
    boolean isOpen();
    Cursor rawQuery(String sql, String[] selectionArgs,
                    CancellationSignal cancellationSignal);
    int update(String table, TableValues values, String whereClause, String[] whereArgs);
    long insert(String table, String nullColumnHack, TableValues values);
}
