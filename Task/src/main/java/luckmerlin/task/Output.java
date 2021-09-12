package luckmerlin.task;

import java.io.IOException;
import java.io.OutputStream;

public class Output extends OutputStream {
    private final OutputStream mOutputStream;
    private final long mLength;
    private boolean mClosed=false;

    public Output(OutputStream outputStream,long length){
        mOutputStream=outputStream;
        mLength=length;
        mClosed=false;
    }

    public final long getLength() {
        return mLength;
    }

    @Override
    public void flush() throws IOException {
        OutputStream stream=mOutputStream;
        if (null!=stream){
            stream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        OutputStream stream=mOutputStream;
        if (null!=stream&&!mClosed){
            mClosed=true;
            stream.close();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        OutputStream stream=mOutputStream;
        if (null!=stream){
            stream.write(b,off,len);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        OutputStream stream=mOutputStream;
        if (null!=stream){
            stream.write(b);
        }
    }

    @Override
    public void write(int b) throws IOException {
        OutputStream stream=mOutputStream;
        if (null!=stream){
            stream.write(b);
        }
    }
}
