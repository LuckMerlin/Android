package com.merlin.file;

public final class Mode {
    public final static int MODE_NORMAL=0x001;
    public final static int MODE_MULTI_CHOOSE=0x002;
    public final static int MODE_DOWNLOAD=0x003;
    public final static int MODE_UPLOAD=0x004;
    public final static int MODE_COPY=0x005;
    public final static int MODE_MOVE=0x006;
    public final static int MODE_SELECT=0x008;
    public final static int MODE_DELETE=0x009;
    private final int mMode;

    public Mode(int mode){
        mMode=mode;
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
}
