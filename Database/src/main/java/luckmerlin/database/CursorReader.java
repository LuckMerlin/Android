package luckmerlin.database;

import java.util.ArrayList;
import java.util.List;
import luckmerlin.core.debug.Debug;

public abstract class CursorReader<T> extends CursorFetcher {

    protected abstract T onRead(Cursor cursor);

    public final List<T> read(Cursor cursor){
           if (null==cursor||!cursor.moveToFirst()){
               return null;
           }
           List<T> messages=new ArrayList<>();
           do{
               T child=onRead(cursor);
               if (null==child){
                   Debug.W("Cursor read NONE.");
                   continue;
               }
               messages.add(child);
           }while (cursor.moveToNext());
           return messages;
   }

}
