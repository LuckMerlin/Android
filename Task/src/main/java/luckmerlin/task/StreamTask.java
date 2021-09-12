package luckmerlin.task;

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
        int cover=mCover;
        Stream streamer=mStreamer;
        if (null==streamer){
            Debug.W("Can't execute stream task while stream NULL.");
            return null;
        }
        try {
            Reply<Output> outputReply=streamer.openOutputStream(cover);
            outputReply=null!=outputReply?outputReply:new Reply<>(Code.CODE_FAIL,"",null);
            Output output=outputReply.isSucceed()?outputReply.getData():null;
            if (null==output){
                Debug.TW("Can't execute stream while open output stream reply fail.",outputReply);
                return new TaskResult(outputReply.getCode(),outputReply.getNote(),null);
            }
            closeOnFinish(output);
            final long skipLength=output.getLength();
            Debug.D("Opened stream task output stream."+cover+" "+skipLength);
            Reply<Input> inputReply=streamer.openInputStream(skipLength);
            inputReply=null!=inputReply?inputReply:new Reply<>(Code.CODE_FAIL,"",null);
            Input input=null!=inputReply&&inputReply.isSucceed()?inputReply.getData():null;
            if (null==input){
                Debug.TW("Can't execute stream while open input stream reply fail.",inputReply);
                return new TaskResult(outputReply.getCode(),outputReply.getNote(),null);
            }
            closeOnFinish(input);
            final long inputLength=input.getLength();
            Debug.D("Opened stream task input stream."+inputLength);
            int bufferSize=mBufferSize;int read=0;
            bufferSize=bufferSize<=0?1024*1024:bufferSize;
            byte[] buffer=new byte[bufferSize];long doneLength=skipLength;
            final LongTypeProgress progress=new LongTypeProgress(inputLength).setDone(doneLength)
                    .setTitle(streamer.getName());
            update(Status.STATUS_DOING,progress);
            Debug.D("Start stream task.");
            while ((read=input.read(buffer))>=0){
                if (read>0){
                    output.write(buffer,0,read);
                    doneLength+=read;
                    progress.setDone(doneLength);
                    update(Status.STATUS_DOING,progress);
                }
            }
            output.flush();
            Debug.D("Finish stream task.");
            return new TaskResult(Code.CODE_SUCCEED,null,null);
        }catch (Exception e){
            Debug.E("Exception execute stream.e="+e,e);
            e.printStackTrace();
            return new TaskResult(Code.CODE_EXCEPTION,"Exception execute stream.e="+e,null);
        }
    }

}
