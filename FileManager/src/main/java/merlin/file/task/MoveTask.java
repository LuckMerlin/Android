package merlin.file.task;

import com.merlin.file.Folder;
import com.merlin.file.Path;
import java.util.List;

public class MoveTask extends CopyTask {

    public MoveTask(List<Path> paths, Folder folder) {
        super(paths,folder);
    }

}
