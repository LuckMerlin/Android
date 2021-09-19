package luckmerlin.task;

import android.os.SystemClock;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.Result;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.match.MatchIterator;
import luckmerlin.core.match.Matchable;
import luckmerlin.core.match.Matcher;

public class TasksExecutor implements TaskRunner {
    private ExecutorService mExecutor;
    private final MatchIterator mMatcher=new MatchIterator();
    private final Set<Tasked> mTaskList;
    private Saver mSaver;
    private Map<OnTaskUpdate,Matchable<Task>> mUpdateMaps;

    public TasksExecutor(ExecutorService executor){
        this(executor,null);
    }

    public TasksExecutor(ExecutorService executor,Comparator<Tasked> comparator){
        mTaskList=new TreeSet<>(null!=comparator?comparator:(Tasked o1, Tasked o2) ->
                Long.compare(o1.mCreateTime,o2.getCreateTime()));
        mExecutor=executor;
    }

    public final TasksExecutor setSaver(Saver saver){
        mSaver=saver;
        return this;
    }

    public final Saver getSaver() {
        return mSaver;
    }

    private void onTaskStatusUpdate(int status, Tasked tasked, Progress progress){
        Map<OnTaskUpdate,Matchable<Task>> updateMaps=mUpdateMaps;
        if (null!=updateMaps&&null!=tasked){
            Set<OnTaskUpdate> set=null;
            synchronized (updateMaps){
                if (null!=(set=updateMaps.keySet())){
                    Integer matched=null;Matchable matchable=null;
                    for (OnTaskUpdate child:set) {
                        if (null!=child&&(null==(matchable=updateMaps.get(child))|| (null!=
                                (matched=matchable.onMatch(tasked.mTask))&&matched==Matchable.MATCHED))){
                            child.onTaskUpdate(status,tasked,progress);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Tasked> start(Object taskObject) {
        if (null==taskObject){
            return null;
        }else if (taskObject instanceof Task){
            Task task=(Task)taskObject;
            Tasked tasked=startTask(task);
            return null!=tasked? Arrays.asList((new Tasked[]{tasked})):null;
        }else if (taskObject instanceof Matchable){
            Matchable matchable=(Matchable)taskObject;
            return iterateKeys(mTaskList,(Tasked arg)-> {
                Task task=null!=arg?arg.mTask:null;
                Integer match=null!=task?matchable.onMatch(task):null;
                return null!=match&&match==Matchable.MATCHED?(null!=startTask(task)?
                        Matchable.MATCHED:Matchable.CONTINUE):match;
            });
        }
        return null;
    }

    private Tasked startTask(Task task){
        final Set<Tasked> taskList=mTaskList;
        if (null==task||null==taskList){
            Debug.W("Can't start task while task NULL.");
            return null;
        }
        List<Tasked> currentTasks=iterateKeys(taskList,new Matcher<Tasked>((arg)->
                null!=arg&&(task==arg||(null!=arg.mTask&&task==arg.mTask))?
                        Matchable.MATCHED:Matchable.CONTINUE).setMax(1));
        Tasked tasked=null!=currentTasks&&currentTasks.size()>0?currentTasks.get(0):null;
        if (null==tasked&&taskList.add(tasked=(task instanceof Tasked? (Tasked)task:new Tasked(task)))) {
            //Do nothing
        }
        final Tasked finalTasked=tasked;
        Runner currentRunner=finalTasked.getRunner();
        if (null!=currentRunner&&null!=currentRunner.getResult()){
            Debug.W("Not need start task while task already finish.");
            return null;
        }
        ExecutorService executor= mExecutor;
        if (null==(executor=(null!=executor?executor: (mExecutor=Executors.newSingleThreadExecutor())))){
            Debug.W("Can't start task while executor invalid.");
            return null;
        }
        final String taskId=finalTasked.mTaskId;
        final Runner runner=new Runner(null!=currentRunner? currentRunner.getProgress():null){
            @Override
            public Runner update(int status, Progress progress) {
                if (mRunning){
                    Saved saved=mSaved;
                    progress=null!=progress?progress:getProgress();
                    if (null!=saved){
                        boolean updated=saved.setProgress(progress);
                        updated|=saved.setStatus(status);
                        Saver saver=mSaver;
                        if (updated&&null!=saver&&saver.save(saved)){
                            //Saved
                        }
                    }
                    super.update(status, progress);
                    TasksExecutor.this.onTaskStatusUpdate(status,finalTasked,progress);
                }
                return this;
            }
        };
        if (null!=taskId&&taskId.length()>0 && task instanceof Savable){
            Saved saved=new Saved();
            saved.setTaskId(taskId).setCreateTime(finalTasked.getCreateTime());
            final Class taskClass=finalTasked.getTaskClass();
            if(saved.setTaskClass(taskClass)){
                Debug.TD("Saved task.",taskClass);
                ((Savable)task).onSave(saved);
                runner.mSaved=saved;
            }else{
                Debug.TW("Fail save task while task class invalid.",taskClass);
            }
        }
        runner.mRunning=true;
        finalTasked.setRunner(runner.update(Status.STATUS_WAIT,null));
        Debug.TD("Wait to start task.",task);
        return null!=runner.setCanceler(executor.submit(()->{
            long time= SystemClock.elapsedRealtime();
            Debug.TD("Start task.",task);
            Result result=task.execute(runner);
            result=null!=result?result:new ReplyResult(Code.CODE_FAIL,"Unknown",null);
            runner.setResult(result);
            long duration=SystemClock.elapsedRealtime()-time;
            Debug.TD("Finish task."+duration,task);
            runner.cleanFinisher(true);
            runner.update(Status.STATUS_IDLE,null);
            runner.mRunning=false;
        }))?tasked:null;
    }

    @Override
    public List<Tasked> cancel(boolean interrupt, Matchable matchable) {
        return null!=matchable?iterateKeys(mTaskList,(Tasked arg)-> {
                Integer match=null!=arg?matchable.onMatch(arg):null;
                if (null!=match&&match==Matchable.MATCHED){
                    Runner runner=arg.getRunner();
                    if (null==runner||null==runner.getResult()){
                        return Matchable.CONTINUE;
                    }
                    Canceler canceler=runner.getCanceler();
                    return null!=canceler&&canceler.cancel(interrupt)?Matchable.MATCHED:Matchable.CONTINUE;
                }
                return match;
        }):null;
    }

    @Override
    public List<Tasked> fetch(Matchable matchable) {
        return iterateKeys(mTaskList,matchable);
    }

    @Override
    public boolean put(OnTaskUpdate callback, Matchable<Task> matchable) {
        if (null!=callback){
            Map<OnTaskUpdate,Matchable<Task>> maps=mUpdateMaps;
            synchronized ((null!=maps?maps:(maps=mUpdateMaps=new HashMap<>()))){
                maps.put(callback,matchable);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(OnTaskUpdate callback) {
        Map<OnTaskUpdate,Matchable<Task>> maps=null!=callback?mUpdateMaps:null;
        if (null==maps){
            return false;
        }
        synchronized (maps){
            maps.remove(callback);
        }
        return true;
    }

    @Override
    public List<Task> delete(Matchable matchable) {
        final List<Task> tasks=null!=matchable?getTasks(matchable):null;
        Set<Tasked> taskeds=mTaskList;
        return null!=tasks&&null!=taskeds?(taskeds.removeAll(tasks)?tasks:null):null;
    }

    @Override
    public boolean add(Object task) {
        if (null==task){
            return false;
        }else if (task instanceof Task){
            Set<Tasked> taskeds=mTaskList;
            return null!=task&&!taskeds.contains(task)&&taskeds.add(task instanceof Tasked?
                    (Tasked)task:new Tasked((Task)task));
        }else if (task instanceof Saved){
            Saved saved=(Saved)task;
            Saver saver=mSaver;
            Task taskInstance=null!=saver?saver.create(saved):null;
            if (null==taskInstance||(taskInstance instanceof Tasked)){
                taskInstance=null;
                try {
                    Constructor constructor=saved.getTaskConstructor();
                    Object instance=null;
                    if(null!=constructor){
                        constructor.setAccessible(true);
                        if (null!=(instance=constructor.newInstance(saved))&& instance instanceof Task){
                            taskInstance=(Task)instance;
                        }
                    }
                } catch (Exception e) {
                    Debug.E("Exception add task from saved.e="+e);
                    e.printStackTrace();
                }
            }
            if (null!=taskInstance&&!(taskInstance instanceof Tasked)){
                Tasked tasked=new Tasked<>(saved.getTaskId(),taskInstance,saved.getCreateTime());
                tasked.setRunner(new Runner(saved.getProgress()).
                        setStatus(Status.STATUS_IDLE).setResult(saved.getResult()));
                return add(tasked);
            }
            return false;
        }
        return false;
    }

    @Override
    public List<Task> getTasks(Matchable matchable) {
        if (null!=matchable){
            Set<Tasked> taskeds=mTaskList;
            final List<Task> tasks=new ArrayList<>();
            iterateKeys(taskeds,(Tasked arg)-> {
                Task childTask=null!=arg?arg.mTask:null;
                Integer matched=null!=childTask?matchable.onMatch(childTask):null;
                if (null!=matched&&matched==Matchable.MATCHED){
                    matched=Matchable.CONTINUE;
                    tasks.add(childTask);
                }
                return matched;
            });
            return tasks;
        }
        return null;
    }

    @Override
    public int getSize() {
        Set<Tasked> taskeds=mTaskList;
        return null!=taskeds?taskeds.size():-1;
    }

    private List<Tasked> iterateKeys(Collection<Tasked> collection, Matchable<Tasked> matchable){
        return null!=collection&&null!=matchable?mMatcher.iterate(collection, matchable):null;
    }
}
