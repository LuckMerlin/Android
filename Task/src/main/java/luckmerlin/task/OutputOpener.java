package luckmerlin.task;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import luckmerlin.core.Reply;
import luckmerlin.core.io.Closer;

public abstract class OutputOpener implements Closeable{
    private final long mLength;
    private Reply<OutputStream> mOpened;

    public OutputOpener(long length){
        mLength=length;
    }

    public final long getLength() {
        return mLength;
    }

    protected abstract Reply<OutputStream> onOpen(long skipLength,long totalLength)throws Exception;

    public final Reply<OutputStream> open(long skipLength,long totalLength) throws Exception{
        if (null!=mOpened){
            return null;
        }
        return mOpened=onOpen(skipLength,totalLength);
    }

    @Override
    public void close() throws IOException {
        Reply<OutputStream> opened=mOpened;
        OutputStream open=null!=opened?opened.getData():null;
        if (null!=open){
            new Closer().close(true,open);
        }
    }
}
