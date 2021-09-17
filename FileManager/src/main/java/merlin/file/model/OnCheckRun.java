package merlin.file.model;

import com.merlin.file.Folder;
import com.merlin.file.Path;

import java.util.List;

public interface OnCheckRun {
    void onCheckRun(Folder folder, List<Path> paths);
}
