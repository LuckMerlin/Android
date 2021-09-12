package merlin.file.task;

import com.merlin.file.Path;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.Running;
import luckmerlin.task.TaskResult;

public class DeleteTask extends PathsTask implements BackgroundTask{

    public DeleteTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecutePath(Path path, Running running) throws Exception {
        if (null==path){
            Debug.W("Can't delete file while path NULL.");
            return new TaskResult(Code.CODE_FAIL,"Path NULL",null);
        }
        String filePath=path.getPath();
        if (null==filePath||filePath.length()<=0){
            Debug.W("Can't delete file while path invalid.");
            return new TaskResult(Code.CODE_FAIL,"Path invalid",null);
        }else if (path.isLocal()){
            return deleteLocalFile(new File(filePath));
        }
        Reply<InputStream>  reply=new NasFetcher().delete(path.getHost(),filePath);
        reply=null!=reply?reply:new Reply<>(Code.CODE_FAIL,"Delete reply NULL",null);
        close(reply.getData());
        return new TaskResult(reply.getCode(),reply.getNote(),null);
    }

    private TaskResult deleteLocalFile(File file){
        return null;
    }
}
