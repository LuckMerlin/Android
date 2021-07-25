package luckmerlin.database;

import java.util.List;
import luckmerlin.core.data.Page;
import luckmerlin.core.debug.Debug;

public abstract class Table<T> extends CursorReader<T> implements Sql {
    private final String mTableName;

    public Table(String tableName){
        mTableName=tableName;
    }

    protected abstract ColumnMap onResolveCreateColumns();

    final int create(Database database){
        if (null==database){
            Debug.W("Can't create table while database NULL.");
            return codeFail(CODE_ARGS);
        }else if (!database.isOpen()){
            Debug.W("Can't create table while database NOT open.");
            return codeFail(CODE_ERROR);
        }else if (isExist(database)){//Already exist
            return codeSucceed(CODE_ALREADY);
        }
        String tableName=mTableName;
        ColumnMap columnMap=onResolveCreateColumns();
        String createColumns=null!=columnMap?columnMap.columns(","):null;
        if (null==createColumns||createColumns.length()<=0){
            Debug.W("Can't create table while create columns INVALID.");
            return codeFail(CODE_ERROR);
        }else if (null==tableName||tableName.length()<=0){
            Debug.W("Can't create table while table name INVALID.");
            return codeFail(CODE_ERROR);
        }
        String sql="create table if not exists "+tableName+" ("+createColumns+")";
        return executeSql(database,sql,false);
    }

    public abstract boolean isExist(Database database);

    public final T queryFirst(Database database,Condition condition,boolean closeDB){
        Page<T> page=query(database, new Query(condition).from(0).size(1), closeDB);
        List<T> data=null!=page?page.getData():null;
        return null!=data&&data.size()>0?data.get(0):null;
    }

    protected void onQueryBefore(Query query){
        //Do nothing
    }

    public final Page<T> query(Database database,Query query,boolean closeDB){
        if (null==query){
            return null;
        }else if (null==database){
            return null;
        }else if (!database.isOpen()){
            return null;
        }
        String tableName=mTableName;
        if (null==tableName||tableName.length()<=0){
            Debug.TW("Fail query db table while table name invalid.", query);
            close(closeDB,database);
            return null;
        }
        query=new Query(query).tableName(tableName);
        onQueryBefore(query);
        int size=query.getSize(-1);
        query.asc(size<0).size(Math.abs(size));
        final long from=query.getFrom();
        String querySql=query.querySql();
        Cursor cursor=null!=querySql&&querySql.length()>0?executeFindSql(database,querySql,false):null;
        if (null==cursor){
            Debug.TW("Can't query while fetch query length fail.", querySql);
            close(closeDB,database);
            return null;
        }
        cursor.moveToFirst();
        long total= cursor.getCount();
        close(true,cursor);
        if (total<=0){
            close(closeDB,database);
            return new Page<>(from,null,total);
        }
        String selectSql=query.selectSql();
        cursor=null!=selectSql&&selectSql.length()>0?executeFindSql(database,selectSql,false):null;
        if (null==cursor){
            Debug.TW("Can't query while cursor fail.", selectSql);
            close(closeDB,database);
            return null;
        }
        Debug.TD(null,"Query db table.total="+total);
        List<T> list=read(cursor);
        close(closeDB,database);
        close(true,cursor);
        return new Page<>(from,list,total);
    }

    public abstract int update(Database database,T data, boolean insertIfNotExist,boolean closeDB);

    public final int insert(Database database, TableValues values,boolean closeDB){
        if (null==database){
            Debug.TW("Fail insert DB data while database invalid.", values);
            return codeFail(CODE_ARGS);
        }else if (!database.isOpen()){
            Debug.TW("Fail insert DB data while database NOT open.", database);
            return codeFail(CODE_ERROR);
        }else if (null==values){
            Debug.TW("Fail insert DB data while values NULL.", database);
            close(closeDB, database);
            return codeFail(CODE_ERROR);
        }
        String tableName=mTableName;
        if (null==tableName||tableName.length()<=0){
            Debug.TW("Fail insert DB data while table name invalid.", tableName);
            close(closeDB, database);
            return codeFail(CODE_ARGS);
        }
        if (values.size()<=0){
            Debug.TW("Fail insert DB data while values set EMPTY.", database);
            close(closeDB, database);
            return codeFail(CODE_EMPTY,CODE_ARGS);
        }
        long id=database.insert(tableName,null,values);
        int code=id>=0?codeSucceed():codeFail();
        close(closeDB, database);
        Debug.TD(isCodeSucceed(code)?"Fail ":"","Insert cache "+this);
        return code;
    }

    public final int update(Database database, TableValues values, String whereClause, String[] whereArgs,boolean closeDB){
        if (null==database){
            Debug.TW("Fail update DB data while database invalid.", values);
            return codeFail(CODE_ARGS);
        }else if (!database.isOpen()){
            Debug.TW("Fail update DB data while database NOT open.", database);
            return codeFail(CODE_ERROR);
        }else if (null==values){
            Debug.TW("Fail update DB data while values NULL.", database);
            close(closeDB, database);
            return codeFail(CODE_ARGS);
        }
        String tableName=mTableName;
        if (null==tableName||tableName.length()<=0){
            Debug.TW("Fail update DB data while table name invalid.", tableName);
            close(closeDB, database);
            return codeFail(CODE_ERROR);
        }
        if (values.size()<=0){
            Debug.TW("Fail update DB data while values set EMPTY.", database);
            close(closeDB, database);
            return codeFail(CODE_EMPTY);
        }
        long id=database.update(tableName,values,whereClause,whereArgs);
        int code=id>=0?codeSucceed(): codeFail();
        close(closeDB, database);
        Debug.TD(code!=CODE_SUCCEED?"Fail ":"","Update cache "+this);
        return code;
    }

    public final String getTableName() {
        return mTableName;
    }

    public final int delete(Database database, Condition condition, boolean closeDB) {
        if (null==database){
            Debug.W("Fail delete message cache while database NULL.");
            return codeFail(CODE_ARGS);
        }else if (!database.isOpen()){
            Debug.W("Fail delete message cache while database NOT open.");
            return codeFail(CODE_ERROR);
        }
        String tableName=mTableName;
        if (null==tableName||tableName.length()<=0){
            Debug.W("Fail delete message cache while table name invalid.");
            close(closeDB,database);
            return codeFail(CODE_ARGS);
        }
        return executeSql(database, "DELETE FROM "+tableName , condition,closeDB);
    }
}
