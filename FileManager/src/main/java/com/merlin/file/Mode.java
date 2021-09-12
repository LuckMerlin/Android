package com.merlin.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mode {
    public final static int MODE_NORMAL=0x001;
    public final static int MODE_DOWNLOAD=0x003;
    public final static int MODE_UPLOAD=0x004;
    public final static int MODE_COPY=0x005;
    public final static int MODE_MOVE=0x006;
    public final static int MODE_SELECT=0x008;
    public final static int MODE_DELETE=0x009;
    public final static int MODE_MULTI_CHOOSE=0x009;
    private List<Path> mMultiChoose;
    private final int mMode;

    public Mode(int mode){
        this(mode,null);
    }

    public Mode(int mode,List<Path> multiChoose ){
        mMode=mode;
        mMultiChoose=multiChoose;
    }

    public final boolean is(int ...modes){
        if (null!=modes&&modes.length>0){
            for (int mode:modes) {
                if (mode==mMode){
                    return true;
                }
            }
        }
        return false;
    }

    public final int getMode() {
        return mMode;
    }

    public final boolean contains(Object object){
        List<Path> multiChoose=null!=object?mMultiChoose:null;
        return null!=multiChoose&&multiChoose.contains(object);
    }

    public final boolean toggle(Path data){
        return null!=data&&(remove(data)||add(data));
    }

    public final List<Path> getList() {
        return mMultiChoose;
    }

    public final boolean addAll(Collection<Path> paths){
        if (null!=paths&&paths.size()>0){
            List<Path> choose=mMultiChoose;
            return (null!=choose?choose:(mMultiChoose=new ArrayList<>())).addAll(paths);
        }
        return false;
    }

    public final boolean add(Path data){
        if (null!=data){
            List<Path> choose=mMultiChoose;
            return (null!=choose?choose:(mMultiChoose=new ArrayList<>())).add(data);
        }
        return false;
    }

    public final int size(){
        List<Path> choose=mMultiChoose;
        return null!=choose?choose.size():-1;
    }

    public final boolean remove(Path data){
        List<Path> choose=null!=data?mMultiChoose:null;
        return null!=choose&&choose.remove(data);
    }

}
