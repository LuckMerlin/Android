package merlin.file.task;

import com.merlin.file.Folder;
import com.merlin.file.Path;
import java.util.List;

public abstract class PathsStreamTask extends PathStreamTask{
    private final Folder mFolder;

    public PathsStreamTask(List<Path> paths, Folder folder) {
        setPaths(paths);
        mFolder=folder;
    }

    public final Folder getFolder() {
        return mFolder;
    }
}
