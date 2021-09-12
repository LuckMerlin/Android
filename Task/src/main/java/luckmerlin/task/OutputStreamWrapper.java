package luckmerlin.task;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamWrapper extends OutputStream {
    private final OutputStream mOutputStream;
    private boolean mClosed=false;

    public OutputStreamWrapper(OutputStream outputStream){
        mOutputStream=outputStream;
        mClosed=false;
    }

    @Override
    public void write(int b) throws IOException {
        OutputStream outputStream=mOutputStream;
        if (null!=outputStream){
            outputStream.write(b);
        }
    }


    @Override
    public void write(byte[] b) throws IOException {
        OutputStream outputStream=mOutputStream;
        if (null!=outputStream){
            outputStream.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        OutputStream outputStream=mOutputStream;
        if (null!=outputStream){
            outputStream.write(b,off,len);
        }
    }

    @Override
    public void flush() throws IOException {
        OutputStream outputStream=mOutputStream;
        if (null!=outputStream){
            outputStream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        OutputStream outputStream=mOutputStream;
        if (null!=outputStream&&!mClosed){
            mClosed=true;
            outputStream.close();
        }
    }
}
