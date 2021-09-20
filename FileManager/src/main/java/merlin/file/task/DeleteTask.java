package merlin.file.task;

import com.merlin.file.Path;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.Progress;
import luckmerlin.task.ReplyResult;
import luckmerlin.task.Runner;
import luckmerlin.task.Status;

public class DeleteTask extends PathsTask implements BackgroundTask{

    public DeleteTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected ReplyResult onExecutePath(Path path, Runner runner) throws Exception {
        if (null==path){
            Debug.W("Can't delete file while path NULL.");
            return new ReplyResult(Code.CODE_FAIL,"Path NULL",null);
        }
        Progress progress=new Progress(100);
        while (true){
            if (path.getName()==null){
                break;
            }
            progress.setDone(progress.getDone()+1);
            if (progress.getProgress()>100){
                progress.setDone(0);
            }
            progress.setTitle("SSSS "+progress.getProgress());
            runner.update(Status.STATUS_DOING,progress);
            Thread.sleep(1000);
        }
        String filePath=path.getPath();
        if (null==filePath||filePath.length()<=0){
            Debug.W("Can't delete file while path invalid.");
            return new ReplyResult(Code.CODE_FAIL,"Path invalid",null);
        }else if (path.isLocal()){
            return deleteLocalFile(new File(filePath));
        }
        Reply<InputStream>  reply=new NasFetcher().delete(path.getHost(),filePath);
        reply=null!=reply?reply:new Reply<>(Code.CODE_FAIL,"Delete reply NULL",null);
        close(reply.getData());
        return new ReplyResult(reply.getCode(),reply.getNote(),null);
    }

    private ReplyResult deleteLocalFile(File file){
        return null;
    }
}
