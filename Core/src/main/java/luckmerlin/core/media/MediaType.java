package luckmerlin.core.media;

public interface MediaType {
    public static final int NONE=0x00;//0x000 0000;
    public static final int VIDEO=0x01;//0x000 0001;
    public static final int AUDIO=0x02;//0x000 0010;
    public static final int LIVE=0x04;//0x0000 0100;
    public static final int SUBTITLE=0x08;//0x0000 1000;
    public static final int RADIO=LIVE|AUDIO;
    public static final int TV=LIVE|AUDIO|VIDEO;
}
