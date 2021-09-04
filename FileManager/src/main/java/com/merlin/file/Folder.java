package com.merlin.file;

import luckmerlin.core.data.Page;

public class Folder<T extends Path> extends Page<T> implements Path {
    private Path mPath;

    public Folder(Path folder,long from,long total){
        super(from,total);
        apply(folder);
    }

    @Override
    public long getFreeSpace() {
        Path path=mPath;
        return null!=path?path.getFreeSpace():-1;
    }

    @Override
    public long getTotalSpace() {
        Path path=mPath;
        return null!=path?path.getTotalSpace():-1;
    }

    public Folder apply(Path path){
        mPath=path;
        return this;
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

    @Override
    public String toString() {
        return "Folder{" +
                "path=" + getPath() +" size="+getSize()+
                '}';
    }
}
