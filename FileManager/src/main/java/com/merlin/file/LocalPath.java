package com.merlin.file;

import java.io.File;

public class LocalPath implements Path {
    private File mFile;

    public final LocalPath apply(File file){
        mFile=file;
        return this;
    }

    @Override
    public long getModifyTime() {
        File file=mFile;
        return null!=file?file.lastModified():-1;
    }

    @Override
    public long getSize() {
        File file=mFile;
        if (null==file||!file.isDirectory()){
            return -1;
        }
        String[] names=file.list();
        return null!=names?names.length:0;
    }

    @Override
    public long getLength() {
        File file=mFile;
        return null!=file?file.length():0;
    }

    @Override
    public String getParent() {
        File file=mFile;
        return null!=file?file.getParent():null;
    }

    @Override
    public String getSep() {
        return File.separator;
    }

    @Override
    public String getName() {
        File file=mFile;
        return null!=file?file.getName():null;
    }
}
