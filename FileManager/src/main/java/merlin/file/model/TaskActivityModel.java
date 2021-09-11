package merlin.file.model;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.view.View;

import com.merlin.file.Path;
import com.merlin.file.TaskService;

import java.util.ArrayList;
import java.util.List;

import luckmerlin.core.debug.Debug;
import luckmerlin.core.match.Matchable;
import luckmerlin.core.service.ServiceConnector;
import luckmerlin.task.OnTaskUpdate;
import luckmerlin.task.Task;
import luckmerlin.task.TaskBinder;
import merlin.file.adapter.TaskListAdapter;
import merlin.file.task.BackgroundTask;
import merlin.file.task.DownloadTask;
import merlin.file.test.TestNasFilePath;

public class TaskActivityModel extends BaseModel implements OnTaskUpdate {
    private final ServiceConnector mConnector=new ServiceConnector();
    private final TaskListAdapter mTaskListAdapter=new TaskListAdapter();

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        bindService(TaskService.class,mConnector.setConnect((ComponentName name, IBinder service)-> {
            if (null!=service&&service instanceof TaskBinder){
                TaskBinder binder=((TaskBinder)service);
                binder.put(TaskActivityModel.this,null);
                mTaskListAdapter.set(binder.getTasks((task)->null!=task&&!(task instanceof BackgroundTask)?
                        Matchable.MATCHED:Matchable.CONTINUE),"After service bind.");
               //Test
                List<Path> paths=new ArrayList<>();
                paths.add(new TestNasFilePath());
                binder.start(new DownloadTask(paths));
            }
        }), Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onTaskUpdate(int status, Task task, Object arg) {

    }

    @Override
    protected void onRootDetached(View view) {
        super.onRootDetached(view);
        TaskBinder binder=mConnector.getBinder(TaskBinder.class);
        if (null!=binder){
            binder.remove(this);
        }
        unbindService(mConnector);
    }

    public final TaskListAdapter getTaskListAdapter() {
        return mTaskListAdapter;
    }
}
