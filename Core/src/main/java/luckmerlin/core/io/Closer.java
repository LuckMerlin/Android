package luckmerlin.core.io;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.io.Closeable;
import java.io.IOException;

public interface Closer {

    default int close(boolean close, Closeable...closeables){
        int count=-1;
        if (close&&null != closeables&&closeables.length>0) {
            count=0;
            for (Closeable child:closeables) {
                if (null==child||(child instanceof Cursor &&((Cursor)child).isClosed())||
                        (child instanceof SQLiteDatabase &&!((SQLiteDatabase)child).isOpen())){
                    continue;
                }
                try {
                    child.close();
                } catch (IOException exception) {
                    //Do nothing
                }
            }
        }
        return count;
    }
}
