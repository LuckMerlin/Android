package luckmerlin.task;

import org.json.JSONObject;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.json.Json;

public final class Saved extends Json {
    private static final String PROGRESS="progress";
    private static final String STATUS="status";
    private static final String TASK_ID="taskId";
    private static final String TASK_CLASS="taskClass";
    private volatile Progress mProgress;
    private volatile Class<?extends Task> mTaskClass;

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
        mTaskClass=cls;
        putJsonValueSafe(this,TASK_CLASS,null!=cls?cls.getName():null);
        return true;
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
        if ((null==progress&&null==current)||(null!=progress&&null!=current&&current!=progress)){
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
