package luckmerlin.database;

import org.json.JSONObject;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.json.Json;

public final class Query extends Json {
    private final String CONDITION="condition";
    private final String COLUMNS="columns";
    private final String SUB_COLUMNS="subColumns";
    private final String TABLE_NAME="tableName";
    private final String FROM="from";
    private final String SIZE="size";
    private final String ORDER_BY="orderBy";
    private final String GROUP_BY="groupBy";
    private final String ASC_ENABLE="asc";

    public Query(){
        super();
    }

    public Query(Condition condition){
        condition(condition);
    }

    public Query(JSONObject query){
        super(query);
    }

    public synchronized final Condition condition(Condition condition){
        condition=null!=condition?condition:getCondition();
        condition=null!=condition?condition:new Condition(null);
        putJsonValueSafe(this,CONDITION,condition.toString());
        return condition;
    }

    public final Query asc(boolean enable){
        return putJsonValueSafe(this,ASC_ENABLE,enable);
    }

    public final boolean isAsc(){
        return optBoolean(ASC_ENABLE,false);
    }

    public final Condition getCondition() {
        String condition=optString(CONDITION,null);
        return null!=condition&&condition.length()>0?new Condition(condition):null;
    }

    public Query groupBy(String groupBy){
        return putJsonValueSafe(this,GROUP_BY,groupBy);
    }

    public String getGroupBy(){
        return getText(GROUP_BY,null);
    }

    public synchronized final Condition condition(){
        return condition(null);
    }

    public synchronized final Columns columns(Columns columns){
        columns=null!=columns?columns:getColumns();
        columns=null!=columns?columns:new Columns(null);
        putJsonValueSafe(this,COLUMNS,columns);
        return columns;
    }

    public final Columns getColumns() {
        JSONObject columns=optJSONObject(COLUMNS);
        return null!=columns?new Columns(columns):null;
    }

    public synchronized final Query subColumns(Columns columns){
        return putJsonValueSafe(this,SUB_COLUMNS,columns);
    }

    public final Columns getSubColumns() {
        JSONObject columns=optJSONObject(SUB_COLUMNS);
        return null!=columns?new Columns(columns):null;
    }

    public final Query tableName(String tableName){
        return putJsonValueSafe(this,TABLE_NAME,tableName);
    }

    public final String getTableName() {
        return getText(TABLE_NAME,null);
    }

    public final Query from(long from){
        return putJsonValueSafe(this,FROM,from);
    }

    public final long getFrom() {
        return optLong(FROM);
    }

    public final Query size(Integer size){
        return putJsonValueSafe(this,SIZE,size);
    }

    public final int getSize(int def) {
        return optInt(SIZE,def);
    }

    public final Query orderBy(String orderBy){
        return putJsonValueSafe(this,ORDER_BY,orderBy);
    }

    public final String getOrderBy() {
        return getText(ORDER_BY,null);
    }

    public final String querySql(){
        String tableName=getTableName();
        if (null==tableName||tableName.length()<=0){
            Debug.W("Can't generate query sql while table name invalid.");
            return null;
        }
        Columns columns=getColumns();
        String columnsText=null!=columns?columns.columns():null;
        Condition condition=getCondition();
        String sql=null!=condition?condition.sql():null;
        final String whereSql=null!=sql&&sql.length()>0?" where "+sql:"";
        //
        Columns subColumns=getSubColumns();
        String subColumnsText=null!=subColumns?subColumns.columns():null;
        return "select "+(null!=columnsText&&columnsText.length()>0?columnsText:"*")+
                (null!=subColumnsText&&subColumnsText.length()>0?","+subColumnsText:"")+
                " from "+tableName+ whereSql;
    }

    public final String selectSql(){
        String querySql=querySql();
        if (null==querySql||querySql.length()<=0){
            Debug.W("Can't generate query select sql while query sql invalid.");
            return null;
        }
        long from=getFrom();
        int size=getSize(-1);
        long querySize=size>0?size:60;
        String orderBy=getOrderBy();
        String groupBy=getGroupBy();
        boolean asc=isAsc();
        return querySql+" "+
                (null!=groupBy&&groupBy.length()>0? " group by "+ groupBy:" ") +
                (null!=orderBy&&orderBy.length()>0? " order by "+ orderBy+(asc?" asc ":" desc "):" ") +
                " limit "+from+","+(from+querySize);
    }
}
