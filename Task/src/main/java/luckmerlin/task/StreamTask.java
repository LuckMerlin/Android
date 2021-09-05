package luckmerlin.task;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import luckmerlin.core.Code;
import luckmerlin.core.debug.Debug;

public abstract class StreamTask extends AbstractTask<TaskResult> {
    private int mBufferSize;
    private int mCover=Cover.NONE;

    public StreamTask(int status,Result result,Progress progress){
        super(status,result,progress);
    }

    public final StreamTask setCover(int cover){
        mCover=cover;
        return this;
    }

    public final int getCover() {
        return mCover;
    }

    public final StreamTask setBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        return this;
    }

    public final int getBufferSize() {
        return mBufferSize;
    }

    protected abstract TaskInputStream openInputStream(Updater<TaskResult> updater);
    protected abstract TaskOutputStream openOutputSteam(Updater<TaskResult> updater);

    @Override
    protected final TaskResult onExecute(Updater<TaskResult> updater) {
        update(Status.STATUS_PREPARE,this,null,updater);
        TaskOutputStream outputSteam=openOutputSteam(updater);
        if (null==outputSteam){
            Debug.TD("Fail open output stream.",this);
            return new TaskResult(Code.CODE_FAIL,"Fail open output stream.",null);
        }
        int cover=mCover;final long doneLength=outputSteam.getSize();
        if (doneLength<=0||cover==Cover.REPLACE||cover==Cover.NONE){
            TaskInputStream inputStream=openInputStream(updater);
            if (null==inputStream){
                Debug.TD("Fail open input stream.",this);
                return new TaskResult(Code.CODE_FAIL,"Fail open input stream.",null);
            }
            try {
                int read=0;long total=inputStream.getSize();
                final Progress progress=new Progress() {
                    @Override
                    public long getDone() {
                        return outputSteam.getSize();
                    }

                    @Override
                    public long getTotal() {
                        return total;
                    }
                };
                update(Status.STATUS_DOING,this,progress,updater);
                //Check for none cover
                if (doneLength>0&&cover==Cover.NONE){
                    if (total==doneLength){
                        Debug.TD("Already done stream task.",this);
                        return new TaskResult(Code.CODE_ALREADY_DONE,"Already done.",null);
                    }else if (total<doneLength){
                        Debug.TD("Already done Not match length stream task.",this);
                        return new TaskResult(Code.CODE_ALREADY|Code.CODE_FAIL,"Already done not match.",null);
                    }else{
                        long skip=inputStream.skip(doneLength);
                        Debug.TD("Skip done length.",doneLength+" "+skip);
                    }
                }
                int bufferSize=mBufferSize;
                bufferSize=bufferSize<=0?1024*1024:bufferSize;
                byte[] buffer=new byte[bufferSize];
                while ((read=inputStream.read(buffer))>=0){
                    if (read>0){
                        outputSteam.write(buffer,0,read);
                        update(Status.STATUS_DOING,this,progress,updater);
                    }
                }
                Debug.TD("Finish stream task.",this);
                return new TaskResult(progress.getProgress()>=100?Code.CODE_SUCCEED:Code.CODE_FAIL,
                        null,null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Debug.TD("Ignore doing exist stream.",this);
            return new TaskResult(Code.CODE_ALREADY,"Ignore exist.",null);
        }
        return null;
    }

    protected final boolean close(Closeable ...closeables){
        if (null!=closeables&&closeables.length>0){
            for (Closeable child:closeables) {
                try {
                    if (null!=child){
                        child.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    public interface Cover{
        int NONE=0;
        int REPLACE=3;
        int IGNORE=4;
    }

    protected static final class TaskInputStream extends InputStream{
        private final InputStream mStream;
        private final long mLength;

        public TaskInputStream(InputStream stream,long length){
            mStream=stream;
            mLength=length;
        }

        public final long getSize(){
            InputStream stream=mStream;
            try {
                return Math.max(null!=stream?stream.available():0,mLength);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mLength;
        }

        @Override
        public void close() throws IOException {
            InputStream stream=mStream;
            if (null!=stream){
                stream.close();
            }
        }

        @Override
        public long skip(long n) throws IOException {
            InputStream stream=mStream;
            return null!=stream?stream.skip(n):-1;
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
        public int read() throws IOException {
            InputStream stream=mStream;
            return null!=stream?stream.read():-1;
        }

        @Override
        public int available() throws IOException {
            InputStream stream=mStream;
            return null!=stream?stream.available():-1;
        }

        @Override
        public synchronized void reset() throws IOException {
            InputStream stream=mStream;
            if (null!=stream){
                stream.reset();
            }
        }

        @Override
        public boolean markSupported() {
            InputStream stream=mStream;
            return null!=stream&&stream.markSupported();
        }

        @Override
        public synchronized void mark(int readlimit) {
            InputStream stream=mStream;
            if (null!=stream){
                stream.mark(readlimit);
            }
        }
    }

    protected static final class TaskOutputStream extends OutputStream{
        private final OutputStream mStream;
        private final long mLength;
        private long mWrite;

        public TaskOutputStream(OutputStream stream,long length){
            mStream=stream;
            mLength=length;
            mWrite=0;
        }

        public long getSize(){
            long length=mLength;
            long write=mWrite;
            return (length<=0?0:length)+(write>=0?write:0);
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (null!=b){
                OutputStream stream=mStream;
                if (null!=stream){
                    stream.write(b);
                    mWrite+=b.length;
                }
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (null!=b&&len>0){
                OutputStream stream=mStream;
                if (null!=stream){
                    stream.write(b,off,len);
                    mWrite+=len;
                }
            }
        }

        @Override
        public void write(int b) throws IOException {
            OutputStream stream=mStream;
            if (null!=stream){
                stream.write(b);
                mWrite++;
            }
        }

        @Override
        public void close() throws IOException {
            OutputStream stream=mStream;
            if (null!=stream){
                stream.close();
            }
        }

        @Override
        public void flush() throws IOException {
            OutputStream stream=mStream;
            if (null!=stream){
                stream.flush();
            }
        }
    }

}
