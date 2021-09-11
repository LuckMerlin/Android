package luckmerlin.task;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.match.Matchable;
import luckmerlin.core.match.MatchIterator;

public class TaskExecutor implements TaskRunner {
    private ExecutorService mExecutor;
    private final MatchIterator mMatcher=new MatchIterator();
    private Map<OnTaskUpdate,Matchable<Task>> mMaps;
    private final Map<Task, Running> mTasksMap=new ConcurrentHashMap<>();
    private final OnTaskUpdate mTaskUpdate=(int status, Task task, Object arg)-> {
        Map<OnTaskUpdate,Matchable<Task>> maps=mMaps;
        if (null!=(null!=maps?mMatcher.iterate(maps.keySet(), (arg1)-> {
            Matchable<Task> matchable=null!=arg1?maps.get(arg1):null;
            Integer matched=null!=matchable?matchable.onMatch(task):Matchable.MATCHED;
            if (null!=matched&&matched==Matchable.MATCHED){
                arg1.onTaskUpdate(status,task,arg);
            }
            return Matchable.CONTINUE;
        }):null)){
            //Do nothing
        }
    };

    @Override
    public boolean put(OnTaskUpdate callback, Matchable<Task> matchable) {
        if (null!=callback){
            Map<OnTaskUpdate,Matchable<Task>> maps=mMaps;
            synchronized ((null!=maps?maps:(maps=mMaps=new HashMap<>()))){
                maps.put(callback,matchable);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(OnTaskUpdate callback) {
        Map<OnTaskUpdate,Matchable<Task>> maps=null!=callback?mMaps:null;
        if (null==maps){
            return false;
        }
        synchronized (maps){
            maps.remove(callback);
        }
        return true;
    }

    @Override
    public List<Task> add(Task... tasks) {
        Map<Task, Running> maps=null!=tasks&&tasks.length>0?mTasksMap:null;
        List<Task> result=null;
        if (null!=maps){
            result=new ArrayList<>();
            for (Task child:tasks) {
                if (null!=child&&!maps.containsKey(child)&&null==maps.put(child, null)){
                    result.add(child);
                }
            }
        }
        return result;
    }

    private boolean startTask(Task task){
        Map<Task, Running> tasksMap=mTasksMap;
        if (null==task||null==tasksMap){
            return false;
        }else if (null==task||null!=tasksMap.get(task)||null!=task.getResult()){
            return false;
        }
        ExecutorService executor= mExecutor;
        if (null==(executor=(null!=executor?executor:(null!=(mExecutor=onCreateExecutorService())?
                mExecutor: Executors.newSingleThreadExecutor())))){
            Debug.W("Can't start task while executor invalid.");
            return false;
        }
        //Check if need auto add into
        final Running running=new Running(mTaskUpdate);
        tasksMap.put(task,running);
        running.update(Status.STATUS_WAIT,task,null);
        Debug.TD("Wait to start task.",task);
        return null!=running.setCanceler(executor.submit(()->{
            long time= SystemClock.elapsedRealtime();
            Debug.TD("Start task.",task);
            TaskResult result=task.execute(running);
            running.setResult(null!=result?result:new TaskResult(Code.CODE_FAIL,"Unknown",null));
            long duration=SystemClock.elapsedRealtime()-time;
            Debug.TD("Finish task."+duration,task);
            running.update(Status.STATUS_IDLE,task,null);
            running.cleanFinisher(true);
        }))||true;
    }

    @Override
    public List<Task> start(Object taskObject) {
        if (null==taskObject){
            return null;
        }else if (taskObject instanceof Task){
            Task task=(Task)taskObject;
            return startTask(task)? Arrays.asList((new Task[]{task})):null;
        }else if (taskObject instanceof Matchable){
            Matchable matchable=(Matchable)taskObject;
            Map<Task, Running> tasksMap=mTasksMap;
            final List<Task> tasks=iterateKeys(tasksMap.keySet(),matchable);
            return iterateKeys(tasks,(Task child)->startTask(child)?Matchable.MATCHED:Matchable.CONTINUE);
        }
        return null;
    }

    @Override
    public List<Task> cancel(boolean interrupt,Matchable matchable) {
        Map<Task, Running> tasksMap=mTasksMap;
        final List<Task> tasks=null!=tasksMap?iterateKeys(tasksMap.keySet(),matchable):null;
        return null!=tasks?iterateKeys(tasks, (Task child)-> {
            Running running=null;
            if (null==child||null==(running=tasksMap.get(child))||null!=child.getResult()){
                return Matchable.CONTINUE;
            }
            Canceler canceler=running.getCanceler();
            return null!=canceler&&canceler.cancel(interrupt)?Matchable.MATCHED:Matchable.CONTINUE;
        }):null;
    }

    @Override
    public List<Task> delete(Matchable matchable) {
        Map<Task, Running> maps=null!=matchable?mTasksMap:null;
        List<Task> tasks=null!=maps?iterateKeys(maps.keySet(),matchable):null;
        return null!=tasks?iterateKeys(tasks, (Task arg)-> (null==arg||maps.get(arg)!=null)?
                Matchable.CONTINUE:null== maps.remove(arg)?Matchable.MATCHED:Matchable.CONTINUE):null;
    }

    @Override
    public int getSize() {
        Map<Task, Running> tasksMap=mTasksMap;
        return null!=tasksMap?tasksMap.size():-1;
    }

    @Override
    public List<Task> getTasks(Matchable matchable) {
        Map<Task, Running> maps=null!=matchable?mTasksMap:null;
        return null!=maps?iterateKeys(maps.keySet(),matchable):null;
    }

    protected ExecutorService onCreateExecutorService(){
        return null;
    }

    private List<Task> iterateKeys(Collection<Task> collection, Matchable<Task> matchable){
        return null!=collection&&null!=matchable?mMatcher.iterate(collection,matchable):null;
    }

}
