package luckmerlin.task;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final Set<Tasked> mTaskList=new LinkedHashSet<>();
    private Map<OnTaskUpdate,Matchable<Task>> mUpdateMaps;

    protected ExecutorService onCreateExecutorService(){
        return null;
    }

    private void onTaskStatusUpdate(int status,Tasked tasked,Progress progress){
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
                null!=arg&&null!=arg.mTask&&task==arg.mTask?
                        Matchable.MATCHED:Matchable.CONTINUE).setMax(1));
        Tasked tasked=null!=currentTasks&&currentTasks.size()>0?currentTasks.get(0):null;
        if (null==tasked&&taskList.add(tasked=new Tasked(task))) {
            //Do nothing
        }
        final Tasked finalTasked=tasked;
        Runner currentRunner=finalTasked.getRunner();
        if (null!=currentRunner&&null!=currentRunner.getResult()){
            Debug.W("Not need start task while task already finish.");
            return null;
        }
        ExecutorService executor= mExecutor;
        if (null==(executor=(null!=executor?executor:(null!=(mExecutor=onCreateExecutorService())?
                mExecutor: Executors.newSingleThreadExecutor())))){
            Debug.W("Can't start task while executor invalid.");
            return null;
        }
        final InnerRunner runner=new InnerRunner(null!=currentRunner? currentRunner.getProgress():null){
            @Override
            public Runner update(int status, Progress progress) {
                if (mRunning){
                    super.update(status, progress);
                    TasksExecutor.this.onTaskStatusUpdate(status,finalTasked,progress);
                }
                return this;
            }
        };
        runner.mRunning=true;
        finalTasked.setRunner(runner.update(Status.STATUS_WAIT,null));
        Debug.TD("Wait to start task.",task);
        return null!=runner.setCanceler(executor.submit(()->{
            long time= SystemClock.elapsedRealtime();
            Debug.TD("Start task.",task);
            Result result=task.execute(runner);
            runner.setResult(null!=result?result:new ReplyResult(Code.CODE_FAIL,"Unknown",null));
            long duration=SystemClock.elapsedRealtime()-time;
            Debug.TD("Finish task."+duration,task);
            runner.update(Status.STATUS_IDLE,null);
            runner.cleanFinisher(true);
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
    public boolean add(Task task) {
        Set<Tasked> taskeds=mTaskList;
        return null!=task&&!taskeds.contains(task)&&taskeds.add(new Tasked(task));
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

    private static class InnerRunner extends Runner{
        private List<Finisher> mFinishers;
        private int mStatus=Status.STATUS_IDLE;
        protected boolean mRunning=false;

        protected InnerRunner(Progress progress) {
            super(progress);
        }

        @Override
        public int getStatus() {
            return mStatus;
        }

        @Override
        public Runner update(int status, Progress progress) {
            mStatus=status;
            if (null!=progress){
                super.setProgress(progress);
            }
            return this;
        }

        protected final Runner cleanFinisher(boolean run){
            List<Finisher> finishers=mFinishers;
            if (null!=finishers){
                if (run){
                    for (Finisher finisher:finishers) {
                        if (null!=finisher){
                            finisher.onFinish(getResult());
                        }
                    }
                }
                finishers.clear();
                mFinishers=null;
            }
            return this;
        }

        @Override
        public final Runner finisher(boolean add, Finisher runnable) {
            if (mRunning&&null != runnable) {
                List<Finisher> finishers = mFinishers;
                finishers=add&&null==finishers?(mFinishers=new ArrayList<>()):finishers;
                if (null!=(finishers=add&&null==finishers?(mFinishers=new ArrayList<>()):finishers)){
                    if (add&&!finishers.contains(runnable)){
                        finishers.add(runnable);
                    }else if (!add&&finishers.remove(runnable)&&finishers.size()<=0){
                        mFinishers=null;
                    }
                }
            }
            return this;
        }
    }
}
