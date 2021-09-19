package com.merlin.file;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import luckmerlin.task.OnTaskUpdate;
import luckmerlin.task.Runner;
import luckmerlin.task.Task;
import luckmerlin.task.TaskBinder;
import luckmerlin.task.Tasked;
import luckmerlin.task.TasksExecutor;

public class TaskService extends Service  implements OnTaskUpdate {
    private final TaskBinder mBinder=new TaskBinder(new TasksExecutor() {
        @Override
        protected ExecutorService onCreateExecutorService() {
            return Executors.newCachedThreadPool();
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mBinder.start(new SaveTaskLoad());
        mBinder.put(this,null);
    }

    @Override
    public void onTaskUpdate(int status, Tasked task, Object arg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinder.remove(this);
    }
}
