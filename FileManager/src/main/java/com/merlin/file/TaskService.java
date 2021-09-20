package com.merlin.file;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.json.Json;
import luckmerlin.task.OnTaskUpdate;
import luckmerlin.task.Saved;
import luckmerlin.task.Saver;
import luckmerlin.task.Task;
import luckmerlin.task.TaskBinder;
import luckmerlin.task.Tasked;
import luckmerlin.task.TasksExecutor;

public class TaskService extends Service  implements OnTaskUpdate,Saver {
    private final TasksExecutor mExecutor= new TasksExecutor(Executors.newCachedThreadPool());
    private final TaskBinder mBinder=new TaskBinder(mExecutor);
    private SharedPreferences mPreference;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences=mPreference=getSharedPreferences("tasks", Context.MODE_PRIVATE);
        Map<String,?> taskMap=null!=preferences?preferences.getAll():null;
        Set<String> mapSet=null!=taskMap?taskMap.keySet():null;
        if (null!=mapSet&&mapSet.size()>0){
            String childText=null;Json childJson=null;
            for (String child:mapSet){
                if (null==(childText=(null!=child? preferences.getString
                        (child,null):null))||childText.length()<=0||
                        null==(childJson=Json.create(childText))||
                        !mBinder.add(new Saved(childJson))){
                    preferences.edit().remove(child).commit();
                    Debug.TW("Remove saved invalid task.",child);
                }
            }
        }
        mExecutor.setSaver(this);
        mBinder.put(this,null);
    }

    @Override
    public Task create(Saved saved) {
        return null;
    }

    @Override
    public boolean save(Saved saved) {
        if (null!=saved&&saved.getTaskClass()!=null){
            String taskId=saved.getTaskId();
            SharedPreferences preferences=null!=taskId?mPreference:null;
            return null!=preferences&&preferences.edit().putString(taskId,saved.toString()).commit();
        }
        return false;
    }

    @Override
    public boolean delete(Saved saved) {
        String taskId=null!=saved?saved.getTaskId():null;
        SharedPreferences preferences=null!=taskId?mPreference:null;
        return null!=preferences&&preferences.edit().remove(taskId).commit();
    }

    @Override
    public void onTaskUpdate(int status, Tasked task, Object arg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPreference=null;
        mBinder.remove(this);
        mExecutor.setSaver(null);
    }
}
