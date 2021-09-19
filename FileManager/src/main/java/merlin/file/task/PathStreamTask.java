package merlin.file.task;

import com.merlin.file.NasPath;
import com.merlin.file.Path;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.Result;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.InputOpener;
import luckmerlin.task.OutputOpener;
import luckmerlin.task.Runner;
import luckmerlin.task.Stream;
import luckmerlin.task.StreamTask;
import luckmerlin.task.ReplyResult;

public abstract class PathStreamTask extends PathsTask{
    private int mCover= Stream.Cover.NONE;
    private int mBufferSize;

    protected final Reply<InputOpener> connectInput(File file) throws Exception{
        if (null==file){
            Debug.W("Can't connect local input while file NULL.");
            return new Reply<>(Code.CODE_ARGS,"File NULL",null);
        }
        return new Reply<>(Code.CODE_SUCCEED, null, new InputOpener(file.length()) {
            @Override
            protected Reply<InputStream> onOpen(long skip) throws Exception{
                if (!file.exists()){
                    Debug.W("Can't open local input while not exist."+file);
                    return new Reply<>(Code.CODE_NOT_EXIST,"File not exist",null);
                }else if (!file.canRead()){
                    Debug.W("Can't open local input while NONE read permission."+file);
                    return new Reply<>(Code.CODE_NONE_ACCESS,"File none read permission",null);
                }
                long length=file.length();
                if (skip<0||skip>length){
                    Debug.W("Can't open local input while skip length invalid."+skip+" "+file);
                    return new Reply<>(Code.CODE_ARGS,"Open input skip length invalid",null);
                }
                FileInputStream inputStream=new FileInputStream(file);
                inputStream.skip(skip);
                return new Reply<>(Code.CODE_SUCCEED,"Succeed",inputStream);
            }
        });
    }

    protected final Reply<InputOpener> connectCloudInput(Path fromFile) throws Exception{
        final String host=null!=fromFile?fromFile.getHost():null;
        final String fromFilePath=null!=fromFile?fromFile.getPath():null;
        if (null==host||host.length()<=0){
            Debug.W("Fail connect cloud input while path host NULL.");
            return new Reply<>(Code.CODE_ARGS,"Input cloud host NULL",null);
        }else if (null==fromFilePath||fromFilePath.length()<=0){
            Debug.W("Fail connect cloud input while path NULL.");
            return new Reply<>(Code.CODE_ARGS,"Input cloud path NULL",null);
        }
        NasFetcher fetcher=new NasFetcher();
        Reply<NasPath> reply=fetcher.fetchNasFile(host,fromFilePath);
        int code=(reply=null!=reply?reply:new Reply<>(Code.CODE_ERROR,"Fetch nas file NULL.",null)).getCode();
        if (code==Code.CODE_SUCCEED){
            NasPath nasPath=reply.getData();
            long length=null!=nasPath?nasPath.getLength():-1;
            if (length<0){
                Debug.W("Fail connect cloud input while reply length invalid.");
                return new Reply<>(Code.CODE_ARGS,"Reply length invalid",null);
            }
            Debug.TD("Connect cloud input.",fromFile);
            return new Reply<>(Code.CODE_SUCCEED, null, new InputOpener(length) {
                @Override
                protected Reply<InputStream> onOpen(long length) throws Exception {
                    return fetcher.openCloudInput(host,fromFilePath,length,-1);
                }
            });
        }
        Debug.W("Fail connect cloud input while fetch fail.");
        return new Reply<>(reply.getCode(),reply.getNote(),null);
    }

    protected final Reply<OutputOpener> connectCloudOutput(Path file, String toFilePath)throws Exception{
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
            final long length=0;
            return new Reply<>(Code.CODE_SUCCEED, null, new OutputOpener(length) {
                @Override
                protected Reply<OutputStream> onOpen(long skipLength,long totalLength) throws Exception {
                    if (skipLength!=length){
                        Debug.W("Can't open cloud output while skip length NOT match out length.");
                        return new Reply<>(Code.CODE_FAIL,"skip length NOT match out length",null);
                    }
                    return fetcher.openCloudOutput(host,toFilePath,skipLength,totalLength);
                }
            });
        }else if (code==Code.CODE_SUCCEED){
            NasPath nasPath=reply.getData();
            final long length=null!=nasPath?nasPath.getLength():-1;
            if (length>=0){
                return new Reply<>(Code.CODE_SUCCEED, null, new OutputOpener(length) {
                    @Override
                    protected Reply<OutputStream> onOpen(long skipLength,long totalLength) throws Exception {
                        if (skipLength!=length){
                            Debug.W("Can't open cloud output while skip length NOT match out length.");
                            return new Reply<>(Code.CODE_FAIL,"skip length NOT match out length",null);
                        }
                        return fetcher.openCloudOutput(host,toFilePath,skipLength,totalLength);
                    }
                });
            }
            Debug.W("Fail open cloud output while fetch file length invalid.");
            return new Reply<>(reply.getCode(),reply.getNote(),null);
        }
        Debug.W("Fail open cloud output while fetch fail.");
        return new Reply<>(reply.getCode(),reply.getNote(),null);
    }

    protected final Reply<OutputOpener> connectOutput(File file){
        if (null==file){
            Debug.W("Can't connect local output while file NULL.");
            return new Reply<>(Code.CODE_ARGS,"File NULL",null);
        }
        return new Reply<>(Code.CODE_SUCCEED, null, new OutputOpener(file.length()) {
            @Override
            protected Reply<OutputStream> onOpen(long skipLength,long totalLength) throws Exception {
                if (!file.exists()){
                    File fileParent=file.getParentFile();
                    if (null!=fileParent&&!fileParent.exists()){
                        fileParent.mkdirs();
                    }
                    file.createNewFile();
                }
                if (!file.exists()){
                    Debug.W("Can't open local output while create file fail.");
                    return new Reply<>(Code.CODE_FAIL,"Create file fail",null);
                }else if (!file.canWrite()){
                    Debug.W("Can't open local output while NONE write permission.");
                    return new Reply<>(Code.CODE_NONE_ACCESS,"NONE write permission",null);
                }else if (skipLength>0&&skipLength!=file.length()){
                    Debug.W("Can't open local output while skip length NOT match out length.");
                    return new Reply<>(Code.CODE_FAIL,"skip length NOT match out length",null);
                }
                return new Reply<>(Code.CODE_SUCCEED,null,new FileOutputStream(file,skipLength>0));
            }
        });
    }

    public final PathsTask setBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        return this;
    }

    public final PathsTask setCover(int cover) {
        this.mCover = cover;
        return this;
    }

    protected abstract Reply<Stream> onCreatePathStreamer(Path fromFile);

    @Override
    protected final Result onExecutePath(Path path, Runner runner) {
        Reply<Stream> streamerReply=null;Stream streamer=null;
        streamerReply=null!=(streamerReply=onCreatePathStreamer(path))?streamerReply:
                new Reply<>(Code.CODE_FAIL,"Fail create path streamer.",null);
        if (null==(streamer=(null!=streamerReply&&streamerReply.isSucceed()?streamerReply.getData():null))){
            Debug.W("Fail execute path stream while create path streamer fail.");
            return new ReplyResult(streamerReply.getCode(),streamerReply.getNote(),null);
        }
        Debug.D("Fetched path stream task stream.");
        Result taskResult=new StreamTask(streamer).setCover(mCover).
                setBufferSize(mBufferSize).execute(runner);
        close(streamer);
        return taskResult;
    }
}
