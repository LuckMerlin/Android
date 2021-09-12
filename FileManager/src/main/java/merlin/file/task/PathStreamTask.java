package merlin.file.task;

import com.merlin.file.NasPath;
import com.merlin.file.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.Input;
import luckmerlin.task.Output;
import luckmerlin.task.Running;
import luckmerlin.task.Stream;
import luckmerlin.task.StreamTask;
import luckmerlin.task.TaskResult;

public abstract class PathStreamTask extends PathsTask{
    private int mCover= Stream.Cover.NONE;
    private int mBufferSize;

    protected final Reply<Input> openInput(File file, long skip) throws Exception{
        if (null==file){
            Debug.W("Can't open local input while file NULL.");
            return new Reply<>(Code.CODE_ARGS,"File NULL",null);
        }
        final long length=file.length();
        if (!file.exists()){
            Debug.W("Can't open local input while not exist."+file);
            return new Reply<>(Code.CODE_NOT_EXIST,"File not exist",null);
        }else if (!file.canRead()){
            Debug.W("Can't open local input while NONE read permission."+file);
            return new Reply<>(Code.CODE_NONE_ACCESS,"File none read permission",null);
        }else if (skip<0||skip>length){
            Debug.W("Can't open local input while skip length invalid."+skip+" "+file);
            return new Reply<>(Code.CODE_ARGS,"Open input skip length invalid",null);
        }
        FileInputStream inputStream=new FileInputStream(file);
        inputStream.skip(skip);
        return new Reply<>(Code.CODE_SUCCEED,null, new Input(inputStream,length));
    }

    protected final Reply<Input> openCloudInput(Path fromFile,long skip) throws Exception{
        final String host=null!=fromFile?fromFile.getHost():null;
        final String fromFilePath=null!=fromFile?fromFile.getPath():null;
        if (null==host||host.length()<=0){
            Debug.W("Fail open cloud input while path host NULL.");
            return new Reply<>(Code.CODE_ARGS,"Input cloud host NULL",null);
        }else if (null==fromFilePath||fromFilePath.length()<=0){
            Debug.W("Fail open cloud input while path NULL.");
            return new Reply<>(Code.CODE_ARGS,"Input cloud path NULL",null);
        }
        NasFetcher fetcher=new NasFetcher();
        Reply<NasPath> reply=fetcher.fetchNasFile(host,fromFilePath);
        int code=(reply=null!=reply?reply:new Reply<>(Code.CODE_ERROR,"Fetch nas file NULL.",null)).getCode();
        if (code==Code.CODE_SUCCEED){
            NasPath nasPath=reply.getData();
            long length=null!=nasPath?nasPath.getLength():-1;
            if (length<skip){
                Debug.W("Fail open cloud input while reply length invalid.");
                return new Reply<>(Code.CODE_ARGS,"Reply length invalid",null);
            }
            return fetcher.openCloudInput(host,fromFilePath,skip);
        }
        Debug.W("Fail open cloud input while fetch fail.");
        return new Reply<>(reply.getCode(),reply.getNote(),null);
    }

    protected final Reply<Output> openCloudOutput(Path file, String toFilePath, int cover)throws Exception{
        final String host=null!=file?file.getHost():null;
        if (null==host||host.length()<=0){
            Debug.W("Fail open cloud output while path or host NULL.");
            return new Reply<>(Code.CODE_ARGS,"Output cloud path or host NULL",null);
        }else if (null==toFilePath||toFilePath.length()<=0){
            Debug.W("Fail open cloud output while path NULL");
            return new Reply<>(Code.CODE_ARGS,"Output cloud file path NULL",null);
        }
        NasFetcher fetcher=new NasFetcher();
        Reply<NasPath> reply=fetcher.fetchNasFile(host,toFilePath);
        int code=(reply=null!=reply?reply:new Reply<>(Code.CODE_ERROR,"Fetch nas file NULL.",null)).getCode();
        if (code==Code.CODE_NOT_EXIST){
            return fetcher.openCloudOutput(host,toFilePath,0);
        }else if (code==Code.CODE_SUCCEED){
            NasPath nasPath=reply.getData();
            long length=null!=nasPath?nasPath.getLength():-1;
            if (length>=0){
                return fetcher.openCloudOutput(host,toFilePath,length);
            }
            Debug.W("Fail open cloud output while fetch file length invalid.");
            return new Reply<>(reply.getCode(),reply.getNote(),null);
        }
        Debug.W("Fail open cloud output while fetch fail.");
        return new Reply<>(reply.getCode(),reply.getNote(),null);
    }

    protected final Reply<Output> openOutput(File file,int cover)throws Exception{
        if (null==file){
            Debug.W("Can't open local output while file NULL.");
            return new Reply<>(Code.CODE_ARGS,"File NULL",null);
        }
        boolean replace=cover== Stream.Cover.REPLACE;
        return new Reply<>(Code.CODE_SUCCEED,null, new Output(new
                FileOutputStream(file, !replace),replace?0:file.length()));
    }

    public final PathsTask setBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        return this;
    }

    public final PathsTask setCover(int cover) {
        this.mCover = cover;
        return this;
    }

    protected abstract Reply<Stream> onCreatePathStreamer(Path path);

    @Override
    protected final TaskResult onExecutePath(Path path, Running running) {
        Reply<Stream> streamerReply=null;Stream streamer=null;
        streamerReply=null!=(streamerReply=onCreatePathStreamer(path))?streamerReply:
                new Reply<>(Code.CODE_FAIL,"Fail create path streamer.",null);
        if (null==(streamer=(null!=streamerReply&&streamerReply.isSucceed()?streamerReply.getData():null))){
            Debug.W("Fail execute path stream while create path streamer fail.");
            return new TaskResult(streamerReply.getCode(),streamerReply.getNote(),null);
        }
        Debug.D("Fetched path stream task stream.");
        TaskResult taskResult=new StreamTask(streamer).setCover(mCover).
                setBufferSize(mBufferSize).execute(running);
        close(streamer);
        return taskResult;
    }
}
