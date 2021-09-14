package merlin.file.task;

import com.merlin.file.Folder;
import com.merlin.file.Path;
import java.util.List;
import luckmerlin.task.Task;

public interface PathTaskCreator {
    Task create(List<Path> paths, Folder folder);
}
