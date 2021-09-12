package merlin.file.task;

import com.merlin.file.Folder;
import com.merlin.file.Path;
import java.util.List;

public class UploadTask extends CopyTask {

    public UploadTask(List<Path> paths, Folder folder) {
        super(paths,folder);
    }

}
