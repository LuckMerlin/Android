package com.merlin.file;

import luckmerlin.core.data.Page;

public class Folder extends Page<Path> implements Path {
    private Path mPath;

    public Folder(Path path){
        apply(path);
    }

    public Folder apply(Path path){
        mPath=path;
        return this;
    }

    public final long getFree() {
        Path path=mPath;
        return 0;
    }

    @Override
    public long getModifyTime() {
        Path path=mPath;
        return null!=path?path.getModifyTime():-1;
    }

    @Override
    public String getMimeType() {
        Path path=mPath;
        return null!=path?path.getMimeType():null;
    }

    @Override
    public final long getSize() {
        Path path=mPath;
        return null!=path?path.getSize():0;
    }

    @Override
    public final long getLength() {
        Path path=mPath;
        return null!=path?path.getModifyTime():-1;
    }

    @Override
    public String getTitle() {
        Path path=mPath;
        return null!=path?path.getTitle():null;
    }

    @Override
    public String getParent() {
        Path path=mPath;
        return null!=path?path.getParent():null;
    }

    @Override
    public String getSep() {
        Path path=mPath;
        return null!=path?path.getSep():null;
    }

    @Override
    public String getName() {
        Path path=mPath;
        return null!=path?path.getName():null;
    }

}
