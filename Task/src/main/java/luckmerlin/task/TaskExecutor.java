package luckmerlin.task;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import luckmerlin.core.Call;
import luckmerlin.core.Code;
import luckmerlin.core.data.Page;
import luckmerlin.core.data.PageFetcher;
import luckmerlin.core.debug.Debug;

public class TaskExecutor implements Executor,TaskGroup {
    private List<Task> mTasks;
    private ExecutorService mExecutor;
    private Task mExecuting;
    private boolean mExecuteDoing=false;

    @Override
    public TaskGroup append(Task task, boolean skipEqual) {
        if (null!=task){
            List<Task> tasks=mTasks;
            synchronized (tasks=null!=tasks?tasks:(mTasks=new ArrayList<>())){
                if ((!skipEqual||!tasks.contains(task))&&tasks.add(task)){
                    //Added
                }
            }
        }
        return this;
    }

    @Override
    public TaskGroup insert(int index, Task task, boolean skipEqual) {
        if (null!=task&&index>=0){
            List<Task> tasks=mTasks;
            synchronized (tasks=null!=tasks?tasks:(mTasks=new ArrayList<>())){
                if ((!skipEqual||!tasks.contains(task))&&index<tasks.size()){
                    tasks.add(index,task);
                }
            }
        }
        return this;
    }

    public final int getSize(){
        List<Task> tasks=mTasks;
        return null!=tasks?tasks.size():-1;
    }

    public final boolean isExist(Object task){
        return null!=task&&indexFirst(task)>=0;
    }

    public final Task findFirst(Object task){
        List<Task> tasks=null!=task?mTasks:null;
        if (null!=tasks){
            synchronized (tasks){
                int index=tasks.indexOf(task);
                return index>=0?tasks.get(index):null;
            }
        }
        return null;
    }

    public final int indexFirst(Object task){
        List<Task> tasks=null!=task?mTasks:null;
        if (null!=tasks){
            synchronized (tasks){
                return tasks.indexOf(task);
            }
        }
        return -1;
    }

    public final boolean removeFirst(Object task){
        List<Task> tasks=null!=task?mTasks:null;
        if (null!=tasks){
            synchronized (tasks){
                if (tasks.remove(task)){
                    if (tasks.size()<=0){
                        mTasks=null;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public final Task getExecuting() {
        return mExecuting;
    }

    @Override
    public Page<Task> getTasks(Task anchor, int limit) {
        List<Task> tasks=mTasks;
        if (null!=tasks){
            synchronized (tasks){
                return new PageFetcher().fromList(anchor, limit, tasks, null,(Task from)-> from);
            }
        }
        return new Page<>().setTotal(0);
    }

    protected ExecutorService onCreateExecutorService(){
        return null;
    }

    @Override
    public final Call execute(Runnable runnable) {
        if (null==runnable){
            return new Call(Code.CODE_ARGS,null,null);
        }
        ExecutorService executor= mExecutor;
        Future future=(null!=executor?executor:(null!=(mExecutor=onCreateExecutorService())?
                mExecutor:Executors.newSingleThreadExecutor())).submit(runnable);
        return new Call(Code.CODE_SUCCEED,null,null).setCanceler(null!=future?(boolean cancel)->
                !future.isDone()&&!future.isCancelled()?future.cancel(true):false:null);
    }

    public synchronized Call start(){
        if (mExecuteDoing){
            Debug.W("Can't start while already started.");
            return null;
        }else if (null==mTasks){
            Debug.W("Not need start while task list EMPTY.");
            return null;
        }
        mExecuteDoing=true;
        return execute(()->{
            long time= SystemClock.elapsedRealtime();
            Debug.D("Start task executor.");
            final OnTaskUpdate callback=(int status, Task task, Object arg)-> {
                Progress progress=null!=task?task.getProgress():null;
                Debug.D("DDDD "+status+" "+(null!=progress?progress.getProgress():-1));
            };
            final Map<Task,TaskResult> doneMap=new HashMap<>();
            while (true){
                Object nextTask=null;
                final List<Task> tasks=mTasks;
                int size=0;
                if (null!=tasks){
                    synchronized (tasks){
                        size=tasks.size();
                        for (Task child:tasks) {
                            if (null==child){
                                tasks.remove(null);
                                nextTask=false;
                                Debug.D("Remove null task from executor.");
                                break;
                            }else if (child.isStatus(Status.STATUS_IDLE)&& !doneMap.containsKey(child)){
                                nextTask=child;
                                Debug.TD("Found next executable task.",child);
                            }
                        }
                    }
                }
                if (null==nextTask){
                    Debug.TD("All task executed."+size,null);
                    break;
                }else if (nextTask instanceof Task){
                    Task executing=mExecuting=((Task)nextTask);
                    Debug.TD("Start execute task.",executing);
                    TaskResult result=executing.execute(callback);
                    mExecuting=null;
                    doneMap.put(executing,null!=result?result:new TaskResult(Code.CODE_FAIL,null,null));
                    Debug.TD("Stop execute task.",executing);
                }
            }
            mExecuteDoing=false;
            Debug.D("Stop task executor.elapsedTime="+(SystemClock.elapsedRealtime()-time));
        });
    }
}
