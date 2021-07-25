package luckmerlin.database.android;

import luckmerlin.database.Cursor;
import luckmerlin.database.Database;
import luckmerlin.database.Table;

public abstract class AndroidTable<T> extends Table<T> {

    public AndroidTable(String tableName) {
        super(tableName);
    }

    public final boolean isExist(Database database){
        String tableName=getTableName();
        if (null!=tableName&&tableName.length()>0){
            Cursor cursor=null;
            try {
                if (null!=(cursor=database.rawQuery("select count(1) from "+tableName,null,null))){
                    return true;
                }
            }catch (Exception e){
                //Do nothing
            }finally {
                close(false,cursor);
            }
        }
        return false;
    }
}
