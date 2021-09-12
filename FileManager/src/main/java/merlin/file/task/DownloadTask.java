package merlin.file.task;

import com.merlin.file.Folder;
import com.merlin.file.Path;
import java.util.List;

public class DownloadTask extends CopyTask {

    public DownloadTask(List<Path> paths, Folder folder) {
        super(paths,folder);
    }

}
