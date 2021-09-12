package merlin.file.task;

import com.merlin.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import luckmerlin.core.Code;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.AbstractTask;
import luckmerlin.task.Running;
import luckmerlin.task.TaskResult;

public abstract class PathsTask extends AbstractTask {
    private Map<Path,TaskResult> mPaths;

    public final PathsTask setPaths(List<Path> paths){
        int size=null!=paths?paths.size():0;
        if (size>0){
            Map<Path,TaskResult> maps=new HashMap<>(size);
            for (Path child:paths) {
                if (null!=child){
                    maps.put(child,null);
                }
            }
            return setPaths(maps);
        }
        return this;
    }

    private final PathsTask setPaths(Map<Path,TaskResult> paths){
        mPaths=paths;
        return this;
    }

    public final Map<Path, TaskResult> getPaths() {
        Map<Path,TaskResult> pathes=mPaths;
        return null!=pathes?new HashMap<>(pathes):null;
    }

    protected abstract TaskResult onExecutePath(Path path,Running running) throws Exception;

    @Override
    protected final TaskResult onExecute(Running running) {
        Map<Path,TaskResult> paths=mPaths;
        final Set<Path> set=null!=paths?paths.keySet():null;
        final int size=null!=set?set.size():-1;
        if (size<=0){
            Debug.W("Can't execute paths task while paths EMPTY.");
            return new TaskResult(Code.CODE_EMPTY,"Paths empty",null);
        }
        try {
            TaskResult taskResult;TaskResult latestFailTaskResult=null;
            Debug.D("Start execute paths task.size="+size);
            for (Path path:set) {
                if (null==path){
                    continue;
                }else if (null!=(taskResult=paths.get(path))){
                    latestFailTaskResult=!taskResult.isSucceed()?taskResult:latestFailTaskResult;
                    continue;
                }
                taskResult=onExecutePath(path,running);
                paths.put(path,null!=taskResult?taskResult:(taskResult=new TaskResult(Code.CODE_FAIL,"",null)));
                latestFailTaskResult=!taskResult.isSucceed()?taskResult:latestFailTaskResult;
            }
            return null!=latestFailTaskResult&&latestFailTaskResult.isSucceed()?
                    new TaskResult(Code.CODE_SUCCEED,"All path succeed.",null):latestFailTaskResult;
        }catch (Exception e){
            Debug.W("Exception execute paths task.e="+e);
            return new TaskResult(Code.CODE_EXCEPTION,"Exception execute paths task.e="+e,null);
        }
    }
}
