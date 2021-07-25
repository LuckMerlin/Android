package luckmerlin.database;

public class Condition {
    private final StringBuffer mBuffer=new StringBuffer();

    public Condition(){
        this(null);
    }

    public Condition(String condition){
        if (null!=condition&&condition.length()>0){
            mBuffer.append(condition);
        }
    }

    public final Condition or(String column, Object value){
        return or(column, value,null);
    }

    public final Condition or(String column, Object value, String equal){
        append("OR",column, value,equal);
        return this;
    }

    public final Condition and(String column, Object value){
        return and(column,value,null);
    }

    public final Condition and(String column, Object value, String equal){
        append("AND",column, value,equal);
        return this;
    }

    public final Condition and(Condition condition){
        append("AND",null!=condition?condition.sql():null);
        return this;
    }

    public final Condition or(Condition condition){
        append("OR",null!=condition?condition.sql():null);
        return this;
    }

    private boolean append(String condition,String column,Object value,String equal){
        if (null!=condition&condition.length()>0&& null!=column&&column.length()>0&&null!=value){
            equal=null!=equal?equal:"=";
            return append(condition,value instanceof Number?column+" "+equal+" "+value:column+"=\""+value+"\"");
        }
        return false;
    }

    private boolean append(String condition,Object value){
        if (null!=condition&&condition.length()>0&&null!=value){
            if (mBuffer.length()>0){
                mBuffer.append(" "+condition+" ");
            }
            mBuffer.append(value);
            return true;
        }
        return false;
    }

    public final String sql() {
        StringBuffer buffer=mBuffer;
        String sqlText=null!=buffer&&buffer.length()>0
                ?buffer.toString():null;
        return null!=sqlText&&sqlText.length()>0?" ( "+sqlText+" ) ":null;
    }

    @Override
    public final String toString() {
        return sql();
    }
}
