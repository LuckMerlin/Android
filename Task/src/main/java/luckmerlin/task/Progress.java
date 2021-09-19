package luckmerlin.task;

import org.json.JSONObject;
import luckmerlin.core.json.Json;

public final class Progress extends Json {
    private static final String DONE="done";
    private static final String TOTAL="total";
    private static final String SPEED="speed";
    private static final String TITLE="title";

    public Progress(long total){
        setTotal(total);
    }

    public Progress(JSONObject jsonObject){
        super(jsonObject);
    }

    public long getDone(){
        return optLong(DONE);
    }

    public Progress setDone(long done){
        return putJsonValueSafe(this,DONE,done);
    }

    public long getTotal(){
        return optLong(TOTAL);
    }

    public Progress setTotal(long total){
        return putJsonValueSafe(this,TOTAL,total);
    }

    public long getSpeed(){
        return optLong(SPEED);
    }

    public Progress setSpeed(long speed){
        return putJsonValueSafe(this,SPEED,speed);
    }

    public String getTitle(){
        return optString(TITLE);
    }

    public Progress setTitle(String title){
        return putJsonValueSafe(this,TITLE,title);
    }

    public final float getProgress(){
        long total=getTotal();
        return total>0?getDone()*100.0f/total:0;
    }
}
