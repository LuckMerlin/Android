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
    private boolean mCheckMd5=false;

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

    public final StreamTask checkMd5(boolean enable){
        mCheckMd5=enable;
        return this;
    }

    public final boolean isCheckMd5() {
        return mCheckMd5;
    }

    public final StreamTask setBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        return this;
    }

    public final int getBufferSize() {
        return mBufferSize;
    }

    protected abstract Input onOpenInput(boolean checkMd5,Updater<TaskResult> updater) throws Exception;
    protected abstract Output onOpenOutput(long inputLength,Updater<TaskResult> updater) throws Exception;

    protected final Updater<TaskResult> addFinishClose(Updater<TaskResult> updater,Closeable ...closeables){
        return null!=updater&&null!=closeables&&closeables.length>0?updater.finishCleaner
                (true,(result)->close(closeables)):updater;
    }

    @Override
    protected final TaskResult onExecute(Updater<TaskResult> updater) {
        try {
            update(Status.STATUS_PREPARE,this,null,updater);
            final boolean checkMd5=isCheckMd5();
            final Input input=onOpenInput(checkMd5,updater);
            if (null==input){
                Debug.TD("Fail open input.",this);
                return new TaskResult(Code.CODE_FAIL,"Fail open input.",null);
            }
            final long inputStreamLength=input.mLength;
            if (checkMd5&&inputStreamLength>0&&input.mMd5==null){//Check input md5 valid
                Debug.TD("Fail execute stream task while input stream md5 invalid.",this);
                return new TaskResult(Code.CODE_FAIL,"Input stream md5 invalid.",null);
            }
            final Output output=onOpenOutput(inputStreamLength,updater);
            if (null==output){
                Debug.TD("Fail open output.",this);
                return new TaskResult(Code.CODE_FAIL,"Fail open output.",null);
            }
            final long[] outputStreamLength=new long[]{output.mLength};
            if (outputStreamLength[0]<0){
                Debug.TD("Fail execute stream while output stream length invalid.",this);
                return new TaskResult(Code.CODE_FAIL,"Output stream length invalid.",null);
            }
            final int cover=mCover;
            if (cover!=Cover.REPLACE){
                if (outputStreamLength[0]==inputStreamLength){
                    if (checkMd5){
                        String inputMd5=input.mMd5;
                        String outputMd5=output.mMd5;
                        if (null==inputMd5||null==outputMd5||!inputMd5.equals(outputMd5)){
                            Debug.TD("Fail execute stream task while already done but md5 not match.",outputMd5);
                            return new TaskResult(Code.CODE_ALREADY|Code.CODE_FAIL,"Already done but md5 not match.",null);
                        }
                        Debug.TD("Stream task md5 matched.",this);
                    }
                    Debug.TD("Already done stream task.",inputStreamLength);
                    return new TaskResult(Code.CODE_ALREADY_DONE,"Stream task already done.",null);
                }else if (outputStreamLength[0]>0&&Cover.IGNORE==cover){
                    Debug.TD("Ignore execute stream task while output stream already exist.",this);
                    return new TaskResult(Code.CODE_ALREADY|Code.CODE_FAIL,"Output already exist.",null);
                }else if (outputStreamLength[0]>inputStreamLength){
                    Debug.TD("Fail execute stream task while output length large than input length.",this);
                    return new TaskResult(Code.CODE_FAIL,"Output length large than input length.",null);
                }
            }else{
                outputStreamLength[0]=0;//Make output stream keep 0 to replace
            }
            update(Status.STATUS_PREPARE,this,null,updater);
            //To open input stream
            InputStream taskInputStream=input.openStream(outputStreamLength[0]);
            if (null==taskInputStream){
                Debug.TD("Fail open input stream.",this);
                return new TaskResult(Code.CODE_FAIL,"Fail open input stream.",null);
            }
            OutputStream outputSteam=output.openStream(outputStreamLength[0]);
            if (null==outputSteam){
                Debug.TD("Fail open output stream.",this);
                return new TaskResult(Code.CODE_FAIL,"Fail open output stream.",null);
            }
            final long[] doneLength=new long[]{0};//Must init as 0
            final Progress progress=new Progress() {
                @Override
                public long getDone() {
                    return outputStreamLength[0]+doneLength[0];
                }

                @Override
                public long getTotal() {
                    return inputStreamLength;
                }
            };
            update(Status.STATUS_DOING,this,progress,updater);
            int bufferSize=mBufferSize;int read=0;
            bufferSize=bufferSize<=0?1024*1024:bufferSize;
            byte[] buffer=new byte[bufferSize];
            while ((read=taskInputStream.read(buffer))>=0){
                if (read>0){
                    outputSteam.write(buffer,0,read);
                    doneLength[0]+=read;
                    update(Status.STATUS_DOING,this,progress,updater);
                }
            }
            Debug.TD("Finish stream task.",this);
            return new TaskResult(progress.getProgress()==100?Code.CODE_SUCCEED:Code.CODE_FAIL,
                    null,null);
        }catch (Exception e){
            e.printStackTrace();
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

    protected static class Input implements StreamOpener<InputStream>{
        private final long mLength;
        private final String mMd5;
        private StreamOpener<InputStream> mOpener;

        public Input(long length,String md5,StreamOpener<InputStream> opener){
            mLength=length;
            mMd5=md5;
            mOpener=opener;
        }

        public InputStream openStream(long skip) throws Exception{
            StreamOpener<InputStream> opener=mOpener;
            return null!=opener?opener.openStream(skip):null;
        }
    }

    protected static class Output implements StreamOpener<OutputStream>{
        private final long mLength;
        private final String mMd5;
        private StreamOpener<OutputStream> mOpener;

        public Output(long length,String md5,StreamOpener<OutputStream> opener){
            mLength=length;
            mMd5=md5;
            mOpener=opener;
        }

        public OutputStream openStream(long skip) throws Exception {
            StreamOpener<OutputStream> opener=mOpener;
            return null!=opener?opener.openStream(skip):null;
        }
    }

    protected interface StreamOpener<T>{
        T openStream(long skip) throws Exception;
    }

//
//    protected static final class TaskInputStream extends InputStream{
//        private final InputStream mStream;
//
//        public TaskInputStream(InputStream stream){
//            mStream=stream;
//        }
//
//        @Override
//        public void close() throws IOException {
//            InputStream stream=mStream;
//            if (null!=stream){
//                stream.close();
//            }
//        }
//
//        @Override
//        public long skip(long n) throws IOException {
//            InputStream stream=mStream;
//            return null!=stream?stream.skip(n):-1;
//        }
//
//        @Override
//        public int read(byte[] b, int off, int len) throws IOException {
//            InputStream stream=mStream;
//            return null!=stream?stream.read(b,off,len):-1;
//        }
//
//        @Override
//        public int read(byte[] b) throws IOException {
//            InputStream stream=mStream;
//            return null!=stream?stream.read(b):-1;
//        }
//
//        @Override
//        public int read() throws IOException {
//            InputStream stream=mStream;
//            return null!=stream?stream.read():-1;
//        }
//
//        @Override
//        public int available() throws IOException {
//            InputStream stream=mStream;
//            return null!=stream?stream.available():-1;
//        }
//
//        @Override
//        public synchronized void reset() throws IOException {
//            InputStream stream=mStream;
//            if (null!=stream){
//                stream.reset();
//            }
//        }
//
//        @Override
//        public boolean markSupported() {
//            InputStream stream=mStream;
//            return null!=stream&&stream.markSupported();
//        }
//
//        @Override
//        public synchronized void mark(int readlimit) {
//            InputStream stream=mStream;
//            if (null!=stream){
//                stream.mark(readlimit);
//            }
//        }
//    }
//
//    protected static final class TaskOutputStream extends OutputStream{
//        private final OutputStream mStream;
//        private final long mLength;
//        private long mWrite;
//
//        public TaskOutputStream(OutputStream stream,long length){
//            mStream=stream;
//            mLength=length;
//            mWrite=0;
//        }
//
//        public long getSize(){
//            long length=mLength;
//            long write=mWrite;
//            return (length<=0?0:length)+(write>=0?write:0);
//        }
//
//        @Override
//        public void write(byte[] b) throws IOException {
//            if (null!=b){
//                OutputStream stream=mStream;
//                if (null!=stream){
//                    stream.write(b);
//                    mWrite+=b.length;
//                }
//            }
//        }
//
//        @Override
//        public void write(byte[] b, int off, int len) throws IOException {
//            if (null!=b&&len>0){
//                OutputStream stream=mStream;
//                if (null!=stream){
//                    stream.write(b,off,len);
//                    mWrite+=len;
//                }
//            }
//        }
//
//        @Override
//        public void write(int b) throws IOException {
//            OutputStream stream=mStream;
//            if (null!=stream){
//                stream.write(b);
//                mWrite++;
//            }
//        }
//
//        @Override
//        public void close() throws IOException {
//            OutputStream stream=mStream;
//            if (null!=stream){
//                stream.close();
//            }
//        }
//
//        @Override
//        public void flush() throws IOException {
//            OutputStream stream=mStream;
//            if (null!=stream){
//                stream.flush();
//            }
//        }
//    }

}
