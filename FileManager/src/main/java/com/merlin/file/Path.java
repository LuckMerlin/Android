package com.merlin.file;

import java.io.File;

public interface Path {

    public long getSize();

    public long getLength();

    public long getTotalSpace();

    public String getHost();

    public long getFreeSpace() ;

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
        String host=getHost();
        return null==host&&null!=path&&path.trim().startsWith(File.separator);
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

    public default String getChildPath(String childName){
        if (null==childName||childName.length()<=0){
            return null;
        }
        String sep=getSep();
        if (null==sep||sep.length()<=0){
            return null;
        }
        String path=getPath();
        if (null==path||path.length()<=0){
            return null;
        }
        return (!path.endsWith(sep)?path+sep:path)+childName;
    }

    public String getParent();

    public String getSep();

    public String getName();

    public default String getPath(){
        String parent=getParent();
        String sep=getSep();
        if (null==sep){
            return null;
        }else if (null==parent){
            parent=sep;
        }else if (!parent.endsWith(sep)){
            parent+=sep;
        }
        String name=getName();
        return null!=name?parent+name:parent;
    }

}
