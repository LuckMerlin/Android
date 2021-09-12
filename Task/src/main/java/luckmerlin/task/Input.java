package luckmerlin.task;

import java.io.IOException;
import java.io.InputStream;

public class Input extends InputStream{
    private final InputStream mStream;
    private final long mLength;
    private boolean mClosed=false;

    public Input(InputStream stream,long length){
        mLength=length;
        mStream=stream;
        mClosed=false;
    }

    public long getLength() {
        return mLength;
    }

    @Override
    public int read() throws IOException {
        InputStream stream=mStream;
        return null!=stream?stream.read():-1;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        InputStream stream=mStream;
        return null!=stream?stream.read(b,off,len):-1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        InputStream stream=mStream;
        return null!=stream?stream.read(b):-1;
    }

    @Override
    public synchronized void reset() throws IOException {
        InputStream stream=mStream;
        if (null!=stream){
            stream.reset();
        }
    }

    @Override
    public long skip(long n) throws IOException {
        InputStream stream=mStream;
        return null!=stream?stream.skip(n):-1;
    }

    @Override
    public synchronized void mark(int readlimit) {
        InputStream stream=mStream;
        if (null!=stream){
            stream.mark(readlimit);
        }
    }

    @Override
    public boolean markSupported() {
        InputStream stream=mStream;
        return null!=stream&&stream.markSupported();
    }

    @Override
    public int available() throws IOException {
        InputStream stream=mStream;
        return null!=stream?stream.available():-1;
    }

    @Override
    public void close() throws IOException {
        InputStream stream=mStream;
        if (null!=stream&&!mClosed){
            mClosed=true;
            stream.close();
        }
    }
}
