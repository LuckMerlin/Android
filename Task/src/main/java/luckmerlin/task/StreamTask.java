package luckmerlin.task;

import java.io.InputStream;
import java.io.OutputStream;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.debug.Debug;

public final class StreamTask extends AbstractTask {
    private int mCover= Stream.Cover.NONE;
    private int mBufferSize;
    private final Stream mStreamer;

    public StreamTask(Stream streamer){
        mStreamer=streamer;
    }

    public final StreamTask setBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        return this;
    }

    public final StreamTask setCover(int cover) {
        this.mCover = cover;
        return this;
    }

    @Override
    protected final TaskResult onExecute(Running running) {
        final int cover=mCover;
        Stream streamer=mStreamer;
        if (null==streamer){
            Debug.W("Can't execute stream task while stream NULL.");
            return null;
        }
        try {
            Reply<InputOpener> inputReply=streamer.connectInputStream();
            inputReply=null!=inputReply?inputReply:new Reply<>(Code.CODE_FAIL,"",null);
            InputOpener inputOpener=null!=inputReply&&inputReply.isSucceed()?inputReply.getData():null;
            if (null==inputOpener){
                Debug.TW("Can't execute stream while open input stream reply fail.",inputReply);
                return new TaskResult(inputReply.getCode(),inputReply.getNote(),null);
            }
            closeOnFinish(inputOpener);
            final long inputLength=inputOpener.getLength();
            Debug.D("Connected stream task input stream."+inputLength);
            if (inputLength<0){
                Debug.TW("Can't execute stream while open input stream length invalid.",inputReply);
                return new TaskResult(inputReply.getCode(),inputReply.getNote(),null);
            }
            //
            Reply<OutputOpener> outputReply=streamer.connectOutputStream();
            outputReply=null!=outputReply?outputReply:new Reply<>(Code.CODE_FAIL,"",null);
            OutputOpener outputOpener=outputReply.isSucceed()?outputReply.getData():null;
            if (null==outputOpener){
                Debug.TW("Can't execute stream while open output stream reply fail.",outputReply);
                return new TaskResult(outputReply.getCode(),outputReply.getNote(),null);
            }
            closeOnFinish(outputOpener);
            final long outputLength=outputOpener.getLength();
            Debug.D("Connected stream task output stream."+cover+" "+outputLength);
            final LongTypeProgress progress=new LongTypeProgress(inputLength).setDone(0).
                    setTitle(streamer.getName());
            update(Status.STATUS_PREPARE,progress);
            if (inputLength==outputLength&&cover!= Stream.Cover.REPLACE){
                Debug.TW("Ignore execute stream while length of stream already matched.",outputReply);
                update(Status.STATUS_PREPARE,progress.setDone(inputLength));
                return new TaskResult(Code.CODE_ALREADY_DONE,"Length already matched",null);
            }else if (outputLength>inputLength){
                Debug.TW("Can't execute stream while output length larger than input.",outputReply);
                return new TaskResult(Code.CODE_FAIL,"Output length larger than input",null);
            }
            final long skipLength=cover== Stream.Cover.REPLACE?0:outputLength;
            update(Status.STATUS_PREPARE,progress.setDone(skipLength));
            //
            Reply<InputStream> inputStreamReply=inputOpener.open(skipLength);
            inputStreamReply=null!=inputStreamReply?inputStreamReply:new Reply<>(Code.CODE_FAIL,"Open fail",null);
            InputStream inputStream=inputStreamReply.getData();
            if (null==inputStream){
                Debug.W("Can't execute stream while open input stream NULL.");
                return new TaskResult(inputStreamReply.getCode(),inputStreamReply.getNote(),null);
            }
            //
            Reply<OutputStream> outputStreamReply=outputOpener.open(skipLength,inputLength);
            outputStreamReply=null!=outputStreamReply?outputStreamReply:new Reply<>(Code.CODE_FAIL,"Open fail",null);
            OutputStream outputStream=outputStreamReply.getData();
            if (null==outputStream){
                Debug.W("Can't execute stream while open output stream NULL.");
                return new TaskResult(outputStreamReply.getCode(),outputStreamReply.getNote(),null);
            }
            //
            int bufferSize=mBufferSize;int read=0;
            bufferSize=bufferSize<=0?1024*1024:bufferSize;
            byte[] buffer=new byte[bufferSize];long doneLength=skipLength;
            update(Status.STATUS_PREPARE,progress);
            Debug.D("Start stream task.");
            while ((read=inputStream.read(buffer))>=0){
                if (read>0){
                    outputStream.write(buffer,0,read);
                    doneLength+=read;
                    progress.setDone(doneLength);
                    update(Status.STATUS_DOING,progress);
                }
            }
            outputStream.flush();
            Debug.D("Finish stream task.");
            return new TaskResult(Code.CODE_SUCCEED,null,null);
        }catch (Exception e){
            Debug.E("Exception execute stream.e="+e,e);
            e.printStackTrace();
            return new TaskResult(Code.CODE_EXCEPTION,"Exception execute stream.e="+e,null);
        }
    }

}
