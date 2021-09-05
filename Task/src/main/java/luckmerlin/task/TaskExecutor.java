package luckmerlin.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import luckmerlin.core.Call;
import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.data.Page;
import luckmerlin.core.data.PageFetcher;

public class TaskExecutor implements Task,Executor,TaskGroup {
    private List<Task> mTasks;
    private ExecutorService mExecutor;

    @Override
    public TaskGroup append(Task task, boolean skipEqual) {
        if (null!=task){
            List<Task> tasks=mTasks;
            synchronized (tasks=null!=tasks?tasks:(mTasks=new ArrayList<>())){
                if ((!skipEqual||!tasks.contains(task))||tasks.add(task)){
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

    @Override
    public Progress getProgress() {
        return null;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public Result getResult() {
        return null;
    }

    @Override
    public Call execute(Object arg, Executor executor, OnTaskUpdate update) {
        List<Task> tasks=mTasks;
        if (null==tasks){
            update(Status.STATUS_FINISH,this,arg,update);
            return null;
        }
        final boolean[] canceled=new boolean[]{false};
        final Canceler canceler=(boolean cancel)-> canceled[0]=cancel;
        synchronized (tasks){
            for (Task task:tasks) {
                if (canceled[0]){
                    update(Status.STATUS_CANCEL,null,null,update);
                    break;
                }else{
                    task.execute(arg,this, update);
                }
            }
        }
        return new Call<>(Code.CODE_SUCCEED,null,null).setCanceler(canceler);
    }
}
