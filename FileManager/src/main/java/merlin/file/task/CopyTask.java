package merlin.file.task;

import com.merlin.file.Folder;
import com.merlin.file.Path;
import java.io.File;
import java.util.List;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.Input;
import luckmerlin.task.Output;
import luckmerlin.task.Stream;

public class CopyTask extends PathStreamTask {
    private final Folder mFolder;

    public CopyTask(List<Path> paths, Folder folder) {
        setPaths(paths);
        mFolder=folder;
    }

    public final Folder getFolder() {
        return mFolder;
    }

    @Override
    protected Reply<Stream> onCreatePathStreamer(Path fromFile) {
        if (null==fromFile){
            Debug.D("Can't start copy task while from file NULL.");
            return new Reply<>(Code.CODE_ARGS,"From file NULL",null);
        }
        final String fromFilePath=fromFile.getPath();
        if (null==fromFilePath||fromFilePath.length()<=0){
            Debug.D("Can't start copy task while from file path invalid.");
            return new Reply<>(Code.CODE_ARGS,"From file path invalid",null);
        }
        final String fileName=fromFile.getName();
        if (null==fileName||fileName.length()<=0){
            Debug.D("Can't start copy task while from file name invalid.");
            return new Reply<>(Code.CODE_ARGS,"From file name invalid",null);
        }
        Folder folder=mFolder;
        final String toFilePath=null!=folder?folder.getChildPath(fileName):null;
        if (null==toFilePath||toFilePath.length()<=0){
            Debug.D("Can't start copy task while to file path invalid.");
            return new Reply<>(Code.CODE_ARGS,"To file path invalid",null);
        }
        return new Reply<>(Code.CODE_SUCCEED, null, new Stream(fileName) {
            @Override
            protected Reply<Input> onOpenInputStream(long skip) throws Exception{
                if (fromFile.isLocal()){
                    return openInput(new File(fromFilePath),skip);
                }
                return openCloudInput(fromFile,skip);
            }

            @Override
            protected Reply<Output> onOpenOutputStream(int cover) throws Exception{
                if (folder.isLocal()){
                    return openOutput(new File(toFilePath),cover);
                }
                return openCloudOutput(folder,toFilePath,cover);
            }
        });
    }
}
