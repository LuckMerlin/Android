package luckmerlin.database;

import luckmerlin.core.Code;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.io.Closer;

public interface Sql extends Code, Closer {

    default int executeSql(Database database,String sql,boolean closeDB){
        if (null==database){
            Debug.TW("Fail execute find sql while database invalid.", sql);
            return codeFail(CODE_ARGS);
        }else if (!database.isOpen()){
            Debug.TW("Fail execute find sql while database NOT open.", sql);
            return codeFail(CODE_ERROR);
        }else if (null==sql||sql.length()<=0){
            Debug.TW("Fail execute find sql while sql invalid.", sql);
            close(closeDB, database);
            return codeFail(CODE_ERROR);
        }
        try {
            database.execSQL(sql,null);
            Debug.TD(null,"Execute sql [ "+sql+"]");
            return codeSucceed();
        }catch (Exception e){
            Debug.E("Exception execute sql.e="+e,e);
            e.printStackTrace();
        }finally {
            close(closeDB, database);
        }
        Debug.TW("Fail execute sql.", sql);
        return codeFail(CODE_UNKNOWN);
    }

    default int executeFindSql(Database database,String sql,Condition condition,boolean closeDB){
        if (null==database){
            Debug.TW("Fail execute find sql while database invalid.", sql);
            return codeFail(CODE_ARGS);
        }else if (!database.isOpen()){
            Debug.TW("Fail execute find sql while database NOT open.", sql);
            return codeFail(CODE_ERROR);
        }
        String conditionSql=null!=condition?condition.sql():null;
        if (null==conditionSql||conditionSql.length()<=0){
            Debug.TW("Fail execute find sql while condition sql invalid.", sql);
            close(closeDB, database);
            return codeFail(CODE_ERROR);
        }
        Cursor cursor=executeFindSql(database,sql=sql+" WHERE "+conditionSql, closeDB);
        if (null==cursor){
            Debug.W("Fail execute find sql while cursor invalid.");
            close(closeDB, database);
            return codeFail(CODE_ERROR);
        }
        close(true,cursor);
        Debug.TD(null,"Finish execute find sql. "+sql);
        close(closeDB, database);
        return codeSucceed();
    }

    default Cursor executeFindSql(Database database,String sql,boolean closeDB){
        if (null==database){
            Debug.TW("Fail execute find sql while database invalid.", sql);
            return null;
        }else if (!database.isOpen()){
            Debug.TW("Fail execute find sql while database NOT open.", sql);
            return null;
        }
        try {
            Debug.TD(null,"[SQL] "+sql);
            return database.rawQuery(sql,null,null);
        }catch (Exception e){
            Debug.E("Exception execute sql "+e,e);
            e.printStackTrace();
            return null;
        }finally {
            close(closeDB, database);
        }
    }

    default int executeSql(Database database,String sql,Condition condition,boolean closeDB){
        if (null==database){
            Debug.TW("Fail execute sql while database invalid.", sql);
            return codeFail(CODE_ARGS);
        }else if (!database.isOpen()){
            Debug.TW("Fail execute sql while database NOT open.", sql);
            return codeFail(CODE_ERROR);
        }
        String conditionSql=null!=condition?condition.sql():null;
        if (null==conditionSql||conditionSql.length()<=0){
            Debug.TW("Fail execute sql while condition sql invalid.", sql);
            close(closeDB,database);
            return codeFail(CODE_ERROR);
        }
        sql=sql+(null==conditionSql||conditionSql.length()<=0?"":" WHERE "+conditionSql);
        Debug.TD(null,"  [SQL] "+sql);
        database.execSQL(sql,null);
        close(closeDB,database);
        return codeSucceed();
    }

}
