package luckmerlin.core;

import org.json.JSONObject;
import luckmerlin.core.json.Json;

public final class JsonResult extends Json implements Result{
    private final static String CODE="code";
    private final static String NOTE="note";
    private final static String DATA="data";

    public JsonResult(int code){
        this(code,null);
    }

    public JsonResult(int code,String note){
        this(code,note,null);
    }

    public JsonResult(int code,String note,JSONObject data){
        setCode(code).setNote(note).setData(data);
    }

    public JsonResult(){
        this(null);
    }

    public JsonResult(JSONObject jsonObject){
        super(jsonObject);
    }

    public JsonResult setCode(int code){
        return putJsonValueSafe(this,CODE,code);
    }

    public int getCode(int def){
        return optInt(CODE,def);
    }

    public boolean isSucceed(){
        return (getCode(Code.CODE_FAIL)&Code.CODE_SUCCEED)>0;
    }

    public String getNote(){
        return optString(NOTE,null);
    }

    public JsonResult setNote(String note){
        return putJsonValueSafe(this,NOTE,note);
    }

    public JsonResult setData(JSONObject object){
        return putJsonValueSafe(this,DATA,object);
    }

    public JSONObject getData(){
        return optJSONObject(DATA);
    }
}
