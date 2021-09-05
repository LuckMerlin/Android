package luckmerlin.task;

public interface Task<T extends TaskResult> extends Status{
    T execute(OnTaskUpdate update);
    Progress getProgress();
    Result getResult();

    int getStatus();

    default float progress(){
        Progress progress=getProgress();
        return null!=progress?progress.getProgress():-1f;
    }

    default boolean isExecuting(){
        return isStatus(STATUS_DOING,STATUS_PREPARE,STATUS_START);
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
        Result result=isStatus(STATUS_FINISH)?getResult():null;
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
