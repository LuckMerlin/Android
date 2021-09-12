package luckmerlin.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamWrapper extends InputStream {
    private final InputStream mInputStream;
    private boolean mClosed=false;

    public InputStreamWrapper(InputStream inputStream){
        mInputStream=inputStream;
        mClosed=false;
    }

    @Override
    public int available() throws IOException {
        InputStream inputStream=mInputStream;
        return null!=inputStream?inputStream.available():-1;
    }

    @Override
    public int read() throws IOException {
        InputStream inputStream=mInputStream;
        return null!=inputStream?inputStream.read():-1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        InputStream inputStream=mInputStream;
        return null!=inputStream?inputStream.read(b):-1;
    }

    @Override
    public long skip(long n) throws IOException {
        InputStream inputStream=mInputStream;
        return null!=inputStream?inputStream.skip(n):0;
    }

    @Override
    public synchronized void mark(int readlimit) {
        InputStream inputStream=mInputStream;
        if (null!=inputStream){
            inputStream.mark(readlimit);
        }
    }

    @Override
    public boolean markSupported() {
        InputStream inputStream=mInputStream;
        return null!=inputStream&&inputStream.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        InputStream inputStream=mInputStream;
        if (null!=inputStream){
            inputStream.reset();
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        InputStream inputStream=mInputStream;
        return null!=inputStream?inputStream.read(b,off,len):-1;
    }

    @Override
    public void close() throws IOException {
        InputStream inputStream=mInputStream;
        if (null!=inputStream&&!mClosed){
            mClosed=true;
            inputStream.close();
        }
    }
}
