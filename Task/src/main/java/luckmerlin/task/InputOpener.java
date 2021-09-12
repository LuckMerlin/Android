package luckmerlin.task;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import luckmerlin.core.Reply;
import luckmerlin.core.io.Closer;

public abstract class InputOpener implements Closeable{
    private final long mLength;
    private Reply<InputStream> mOpened;

    public InputOpener(long length){
        mLength=length;
    }

    public final long getLength() {
        return mLength;
    }

    protected abstract Reply<InputStream> onOpen(long skipLength)throws Exception;

    public final Reply<InputStream> open(long skipLength) throws Exception{
        if (null!=mOpened){
            return null;
        }
        return mOpened=onOpen(skipLength);
    }

    @Override
    public void close() throws IOException {
        Reply<InputStream> opened=mOpened;
        InputStream open=null!=opened?opened.getData():null;
        if (null!=open){
            new Closer().close(true,open);
        }
    }
}
