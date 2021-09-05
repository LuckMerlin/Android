package com.merlin.file;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import luckmerlin.task.TaskBinder;
import luckmerlin.task.TaskExecutor;

public class TaskService extends Service {
    private final TaskBinder mBinder=new TaskBinder().setGroup(new TaskExecutor() {
        @Override
        protected ExecutorService onCreateExecutorService() {
            return Executors.newCachedThreadPool();
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
