package luckmerlin.task;

import org.json.JSONObject;
import java.lang.reflect.Constructor;
import luckmerlin.core.JsonResult;
import luckmerlin.core.Result;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.json.Json;

public final class  Saved extends Json {
    private static final String PROGRESS="progress";
    private static final String STATUS="status";
    private static final String TASK_ID="taskId";
    private static final String RESULT="result";
    private static final String TASK_CLASS="taskClass";
    private static final String CREATE_TIME="createTime";
    private volatile Progress mProgress;
    private volatile Class<?extends Task> mTaskClass;
    private volatile JsonResult mResult;

    public Saved(){
        this(null);
    }

    public Saved(JSONObject jsonObject){
        super(jsonObject);
        mProgress=getProgress();
    }

    public final String getTaskId(){
        return optString(TASK_ID,null);
    }

    protected final Saved setTaskId(String taskId){
        return putJsonValueSafe(this,TASK_ID,taskId);
    }

    protected final Saved setCreateTime(long createTime){
        return putJsonValueSafe(this,CREATE_TIME,createTime);
    }

    public final long getCreateTime() {
        return optLong(CREATE_TIME,-1);
    }

    protected final boolean setResult(JsonResult result){
        Result current=mResult;
        if ((null==current&&null==result)||(null!=current&&null!=result&&current!=result)){
            return false;
        }
        putJsonValueSafe(this,RESULT,mResult=result);
        return true;
    }

    public final JsonResult getResult(){
        JSONObject jsonObject= optJSONObject(RESULT);
        return null!=jsonObject?new JsonResult(jsonObject):null;
    }

    protected final boolean setStatus(int status){
        if (status!=getStatus()){
            putJsonValueSafe(this,STATUS,status);
        }
        return true;
    }

    public final int getStatus() {
        return optInt(STATUS,Status.STATUS_IDLE);
    }

    protected final boolean setTaskClass(Class<?extends Task> cls){
        Class<?extends Task> current=mTaskClass;
        if ((null==cls&&null==current)||(null!=cls&&null!=current&&current.equals(cls))){
            return false;
        }
        try {
            if (null==cls||null!=getTaskConstructor(cls)){
                mTaskClass=cls;
                putJsonValueSafe(this,TASK_CLASS,null!=cls?cls.getName():null);
                return true;
            }
        } catch (Exception e) {
            Debug.E("Exception set task class.e="+e);
            e.printStackTrace();
        }
        return false;
    }

    protected final Constructor getTaskConstructor() throws Exception{
        return getTaskConstructor(getTaskClass());
    }

    private final Constructor getTaskConstructor(Class taskClass) throws Exception{
        Constructor[] constructors=null!=taskClass?taskClass.getDeclaredConstructors():null;
        if (null!=constructors&&constructors.length>0){
            Class[] parameterTypes=null;Class childTypes=null;Object instance=null;
            for (Constructor constructor:constructors){
                if (null!=(parameterTypes=null!=constructor?
                        constructor.getParameterTypes():null)&&
                        parameterTypes.length==1&&
                        null!=(childTypes=parameterTypes[0])&&
                        Saved.class.isAssignableFrom(childTypes)){
                    return constructor;
                }
            }
        }
        return null;
    }

    public final Class<?extends Task> getTaskClass() {
        Class<?extends Task> current=mTaskClass;
        if (null==current){
            String clsName=optString(TASK_CLASS);
            try {
                Class cls= null!=clsName&&clsName.length()>0?Class.forName(clsName):null;
                return null!=cls&&Task.class.isAssignableFrom(cls)?(Class<?extends Task>)cls:null;
            } catch (Exception e) {
                Debug.E("Exception get task class.e="+e);
                e.printStackTrace();
            }
        }
        return current;
    }

    protected final boolean setProgress(Progress progress){
        Progress current=mProgress;
        if ((null==progress&&null==current)||(null!=progress&&null!=current&&
                (current==progress&&current.getProgress()==progress.getProgress()))){
            return false;
        }
        putJsonValueSafe(this,PROGRESS,mProgress=progress);
        return true;
    }

    public final Progress getProgress() {
        Progress progress=mProgress;
        if (null==progress){
            JSONObject jsonObject=optJSONObject(PROGRESS);
            progress=mProgress=null!=jsonObject?new Progress(jsonObject):null;
        }
        return progress;
    }
}
