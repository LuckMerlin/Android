package merlin.file.util;

import android.webkit.MimeTypeMap;

public class Thumbs {

    public final String getExtension(String path){
        int index=null!=path&&path.length()>=0?path.lastIndexOf("."):-1;
        return index>=0?path.substring(index):null;
    }

    public final boolean isImageExtension(String extension){
        return null!=extension&&extension.length()>0&&(extension.equalsIgnoreCase(".jpg")||extension.equalsIgnoreCase(".jpeg")||
                extension.equalsIgnoreCase(".gif"));
    }

    public final boolean isAudioExtension(String extension){
        return null!=extension&&extension.length()>0&&(extension.equalsIgnoreCase(".ogg")||extension.equalsIgnoreCase(".amr")||
                extension.equalsIgnoreCase(".mp3"));
    }

    public final boolean isVideoExtension(String extension){
        return null!=extension&&extension.length()>0&&(extension.equalsIgnoreCase(".mp4")||extension.equalsIgnoreCase(".mkv")||
                extension.equalsIgnoreCase(".flv"));
    }

    public final String getMimeType(String path){
        String extension=null!=path&&path.length()>0?getExtension(path):null;
        return null!=extension&&extension.length()>0?MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                extension.toLowerCase()):null;
    }

    public final String getThumb(String path){
        String extension=null!=path&&path.length()>0?getExtension(path):null;
        return null!=extension&&extension.length()>0?(isVideoExtension(extension)||
                extension.equalsIgnoreCase(".mp3")||isImageExtension(extension))?path:null:null;
    }

}
