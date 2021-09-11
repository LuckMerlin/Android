package merlin.file.task;

import com.merlin.file.Path;

import java.util.List;
import luckmerlin.task.AbstractTask;
import luckmerlin.task.Execute;
import luckmerlin.task.TaskResult;

public abstract class PathsTask<T extends TaskResult> extends AbstractTask<T> {
    private List<Path> mPaths;

    public PathsTask(Execute execute) {
        super(execute);
    }

    public final PathsTask<T> setPaths(List<Path> paths){
        mPaths=paths;
        return this;
    }
}
