package luckmerlin.task;

import luckmerlin.core.Result;

public interface Task<T extends Result> extends Execute,  Status{

    T execute(Running running);

    String getName();

    default float progress(){
        Progress progress=getProgress();
        return null!=progress?progress.getProgress():-1f;
    }

    default boolean isExecuting(){
        return !isStatus(STATUS_IDLE);
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
}
