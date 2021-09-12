package luckmerlin.task;

import java.io.Closeable;
import java.io.IOException;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.io.Closer;

public abstract class Stream extends Closer implements Closeable {
    private String mName;
    private Reply<InputOpener> mInputReply;
    private Reply<OutputOpener> mOutReply;

    public Stream(String name){
        mName=name;
    }

    public interface Cover{
        int NONE=0;
        int REPLACE=3;
        int IGNORE=4;
    }

    protected abstract Reply<InputOpener> onConnectInputStream() throws Exception;

    public final Reply<InputOpener> connectInputStream()throws Exception{
        if (null!=mInputReply){
            return new Reply<>(Code.CODE_FAIL,"Already connected.",null);
        }
        return mInputReply=onConnectInputStream();
    }

    protected abstract Reply<OutputOpener> onConnectOutputStream()throws Exception;

    public final Reply<OutputOpener> connectOutputStream()throws Exception{
        if (null!=mOutReply){
            return new Reply<>(Code.CODE_FAIL,"Already connected.",null);
        }
        return mOutReply=onConnectOutputStream();
    }

    public final String getName(){
        return mName;
    }

    @Override
    public void close() throws IOException {
        Reply<InputOpener> inputReply=mInputReply;
        InputOpener inputOpener=null!=inputReply?inputReply.getData():null;
        Reply<OutputOpener> outReply=mOutReply;
        OutputOpener outputOpener=null!=outReply?outReply.getData():null;
        close(true,inputOpener,outputOpener);
    }

}
