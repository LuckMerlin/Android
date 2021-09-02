package com.merlin.file;

import java.io.File;

public interface Path {

    public long getSize();

    public long getLength();

    public default boolean isDirectory(){
        return getSize()>=0;
    }

    String getMimeType();

    public default boolean isAccess(){
        return true;
    }

    public long getModifyTime();

    public default int getPermissions(){
        return 0;
    }

    public default String getPermission(){
        return null;
    }

    public default boolean isLocal(){
        String path=getPath();
        return null!=path&&path.trim().startsWith(File.separator);
    }

    public default String getTitle(){
        String name=getName();
        String sep=getSep();
        return null!=name&&null!=sep?name.replaceFirst(sep,""):null;
    }

    public default String getExtension(){
        String fileName=getName();
        int length=null!=fileName?fileName.length():-1;
        int index=length>=0?fileName.lastIndexOf("."):-1;
        return index>=0&&index<length?fileName.substring(index):null;
    }

    public String getParent();

    public String getSep();

    public String getName();

    public default String getPath(){
        String parent=getParent();
        String sep=getSep();
        String name=getName();
        return (null!=parent?parent:"")+(null!=sep?sep:"")+(null!=name?name:"");
    }

}
