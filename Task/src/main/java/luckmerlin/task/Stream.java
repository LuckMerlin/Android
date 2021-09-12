package luckmerlin.task;

import java.io.Closeable;
import java.io.IOException;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.io.Closer;

public abstract class Stream extends Closer implements Closeable {
    private String mName;
    private Reply<Input> mInputReply;
    private Reply<Output> mOutReply;

    public Stream(String name){
        mName=name;
    }

    public interface Cover{
        int NONE=0;
        int REPLACE=3;
        int IGNORE=4;
    }

    protected abstract Reply<Input> onOpenInputStream(long skip) throws Exception;

    public final Reply<Input> openInputStream(long skip)throws Exception{
        if (null!=mInputReply){
            return new Reply<>(Code.CODE_FAIL,"Already opened.",null);
        }
        return mInputReply=onOpenInputStream(skip);
    }

    protected abstract Reply<Output> onOpenOutputStream(int cover)throws Exception;

    public final Reply<Output> openOutputStream(int cover)throws Exception{
        if (null!=mOutReply){
            return new Reply<>(Code.CODE_FAIL,"Already opened.",null);
        }
        return mOutReply=onOpenOutputStream(cover);
    }

    public final String getName(){
        return mName;
    }

    @Override
    public void close() throws IOException {
        Reply<Input> inputReply=mInputReply;
        Input input=null!=inputReply?inputReply.getData():null;
        Reply<Output> outReply=mOutReply;
        Output output=null!=outReply?outReply.getData():null;
        close(true,input,output);
    }

}
