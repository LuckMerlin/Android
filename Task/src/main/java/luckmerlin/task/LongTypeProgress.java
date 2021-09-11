package luckmerlin.task;

public class LongTypeProgress implements Progress{
    private final long mTotal;
    private long mDone;
    private long mSpeed;
    private String mName;

    public LongTypeProgress(long total){
        mTotal=total;
    }

    public final LongTypeProgress setDone(long done) {
        this.mDone = done;
        return this;
    }

    public final LongTypeProgress setSpeed(long speed) {
        this.mSpeed = speed;
        return this;
    }

    public final LongTypeProgress setTitle(String title) {
        this.mName = title;
        return this;
    }

    @Override
    public final String getTitle() {
        return mName;
    }

    @Override
    public final long getDone() {
        return mDone;
    }

    @Override
    public final long getTotal() {
        return mTotal;
    }

    @Override
    public final long getSpeed() {
        return mSpeed;
    }
}
