package merlin.file.task;

import com.merlin.file.Path;
import java.util.List;
import java.util.Random;
import luckmerlin.task.LongTypeProgress;
import luckmerlin.task.Running;
import luckmerlin.task.TaskResult;

public class DownloadTask extends PathsTask {

    public DownloadTask(List<Path> paths) {
        setPaths(paths);
    }

    @Override
    protected TaskResult onExecute(Running running) {
        update(STATUS_PREPARE,null);
        LongTypeProgress progress=new LongTypeProgress(10000);
        while (true){
            if (running==null){
                break;
            }
            progress.setSpeed(new Random().nextInt(1000000));
            progress.setDone(new Random().nextInt(10000));
            progress.setTitle("是的发送到发"+new Random().nextDouble());
            update(STATUS_DOING,progress);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                //Do nothing
            }
        }
        return null;
    }
}
