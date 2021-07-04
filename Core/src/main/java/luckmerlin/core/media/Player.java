package luckmerlin.core.media;

import android.graphics.SurfaceTexture;

import org.json.JSONObject;

public interface Player {
    boolean pre(Object media,Double seek);
    boolean next(Object media,Double seek);
    boolean setDisplay(SurfaceTexture holder,int width,int height);
    boolean setSchedule(Schedule schedule);
    Schedule getSchedule();
    Player setQueue(Queue<Media> queue);
    Queue<Media> getQueue();
    boolean stop();
    boolean pause();
    Media getPlaying();
    JSONObject getPlayingMeta();
    int getStatus();
    long getCurrentMillisecond();
    long getTotalMillisecond();
    boolean play(Object media,Double seek,OnMediaUpdate callback);
    OnMediaUpdate getCallback(Object callback);
    boolean remove(OnMediaUpdate callback);
    boolean add(OnMediaUpdate callback);
    boolean seek(Double seek);
    boolean destroy();
}
