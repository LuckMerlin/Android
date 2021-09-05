package com.merlin.file;

import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import luckmerlin.core.debug.Debug;

public class LocalPath implements Path {
    private File mFile;

    public final LocalPath apply(File file){
        mFile=file;
        return this;
    }

    @Override
    public long getTotalSpace() {
        File file=mFile;
        return null!=file?file.getTotalSpace():-1;
    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public long getFreeSpace() {
        File file=mFile;
        return null!=file?file.getFreeSpace():-1;
    }

    @Override
    public long getModifyTime() {
        File file=mFile;
        return null!=file?file.lastModified():-1;
    }

    @Override
    public String getMimeType() {
        String path=getPath();
        if (null!=path&&path.length()>0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    return Files.probeContentType(Paths.get(path));
                } catch (IOException e) {
                    //Do nothing
                }
            }else{

            }
        }
        return null;
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
