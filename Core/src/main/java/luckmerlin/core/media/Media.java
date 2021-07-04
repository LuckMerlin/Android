package luckmerlin.core.media;

import org.json.JSONObject;
import luckmerlin.core.json.Json;

public class Media extends Json {
    final static String PATH="path";
    final static String TITLE="title";
    final static String NAME="name";
    final static String ARTIST="artist";
    final static String ALBUM="album";
    final static String DURATION="duration";
    final static String AVATAR="avatar";
    final static String MIME="mime";
    final static String NOTE="note";

    public Media(){
        this(null);
    }

    public Media(JSONObject jsonObject){
        super(jsonObject);
    }

    public final Media setPath(String path){
        return putJsonValueSafe(this,PATH,path);
    }

    public final Media setTitle(String title){
        return putJsonValueSafe(this,TITLE,title);
    }

    public final Media setName(String name){
        return putJsonValueSafe(this,NAME,name);
    }

    public final Media setDuration(long duration){
        return putJsonValueSafe(this,DURATION,duration);
    }

    public final Media setArtist(String artist){
        return putJsonValueSafe(this,ARTIST,artist);
    }

    public final Media setAvatar(String avatar){
        return putJsonValueSafe(this,AVATAR,avatar);
    }

    public final Media setAlbum(String album){
        return putJsonValueSafe(this,ALBUM,album);
    }

    public final String getPath(){
        return getText(PATH,null);
    }

    public final String getTitle(){
        return getText(TITLE,null);
    }

    public final String getName(){
        return getText(NAME,null);
    }

    public final String getArtist(){
        return getText(ARTIST,null);
    }

    public final String getAlbum(){
        return getText(ALBUM,null);
    }

    public final String getAvatar(){
        return getText(AVATAR,null);
    }

    public final String getMime(){
        return getText(MIME,null);
    }

    public final String getNote(){
        return getText(NOTE,null);
    }

    public final long getDuration(){
        return optLong(DURATION,-1);
    }

    @Override
    public boolean equals(Object o) {
        if (null!=o&&o instanceof String){
            String path=getPath();
            return null!=path&&path.equals(o);
        }
        return super.equals(o);
    }
}
