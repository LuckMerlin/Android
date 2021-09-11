package luckmerlin.task;

public interface Task<T extends TaskResult> extends Execute,  Status{

    T execute(OnTaskUpdate update);

    default float progress(){
        Progress progress=getProgress();
        return null!=progress?progress.getProgress():-1f;
    }

    default boolean isExecuting(){
        return isStatus(STATUS_DOING,STATUS_PREPARE);
    }

    default boolean isStatus(int ...statuses){
        int length=null!=statuses?statuses.length:-1;
        if (length>0){
            int status=getStatus();
            for (int i = 0; i < length; i++) {
                if (statuses[0]==status){
                    return true;
                }
            }
        }
        return false;
    }

    default boolean isSucceed(){
        Result result=getResult();
        return null!=result&&result.isSucceed();
    }

    default boolean update(int status, Task task,Object arg,OnTaskUpdate update){
        if (null!=update){
            update.onTaskUpdate(status,task,arg);
            return true;
        }
        return false;
    }
}
