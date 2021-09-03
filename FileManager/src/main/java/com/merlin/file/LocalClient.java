package com.merlin.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.data.OnPageLoadFinish;
import luckmerlin.core.debug.Debug;

public class LocalClient extends Client<Path,Path> {

    public LocalClient(String name) {
        super(name);
    }

    @Override
    public boolean open(Context context, Path path) {
        String filePath=null!=context&&null!=path?path.getPath():null;
        if (null!=filePath&&filePath.length()>0){
            try {
                Intent intent = new Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).
                        setAction(Intent.ACTION_VIEW).setDataAndType(Uri.
                        fromFile(new File(filePath)), path.getMimeType());
                context.startActivity(intent);
                return true;
            }catch (Exception e){
                Debug.E("Exception open path.e="+e);
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Canceler onLoad(Path folder, Path anchor, int limit, OnPageLoadFinish<Path> callback) {
        if (null==(folder=null!=folder?folder:getHome())){
            Debug.W("Can't load local folder while folder invalid.");
            notifyFinish(Code.CODE_ARGS,"Args invalid",null,callback);
            return null;
        }
        String path=folder.getPath();
        File file=null!=path&&path.length()>0?new File(path):null;
        Reply<Folder> reply=null;
        if (null==file){
            Debug.W("Can't load local folder while folder NULL.");
            notifyFinish(Code.CODE_ARGS,"Args invalid",null,callback);
            return null;
        }else if (!file.exists()){
            Debug.W("Can't load local folder while folder not exist. "+file);
            notifyFinish(Code.CODE_NOT_EXIST,"Directory not exist.",null,callback);
            return null;
        }else if (!file.isDirectory()){
            Debug.W("Can't load local folder while folder not directory.");
            notifyFinish(Code.CODE_ARGS,"Not directory",null,callback);
            return null;
        }else if (!file.canRead()){
            Debug.W("Can't load local folder while folder none permission.");
            notifyFinish(Code.CODE_NONE_ACCESS,"Folder none permission",null,callback);
            return null;
        }
        final File[] files=file.listFiles();
        final int length=null!=files?files.length:0;
        final Folder browseFolder=new Folder(new LocalPath().apply(file));
        browseFolder.setFreeSpace(file.getFreeSpace()).setTotalSpace(file.getTotalSpace()).setTotal(length);
        if (length<=0){
            notifyFinish(Code.CODE_SUCCEED,"Directory empty",browseFolder,callback);
            return null;
        }else if (limit==0){
            Debug.W("Can't load local folder while limit invalid.");
            notifyFinish(Code.CODE_FAIL,"Limit invalid",browseFolder,callback);
            return null;
        }
        final Comparator<File> comparator=(File o1, File o2)->null!=o1&&o1.isDirectory()
                ?-1:null!=o2&&o2.isDirectory()?1:o1.compareTo(o2);
        Arrays.sort(files,comparator);
        String anchorPath=null!=anchor?anchor.getPath():null;
        int anchorIndex=limit>0?0:length-1;
        final int shift=limit>0?1:-1;
        if (null!=anchorPath&&anchorPath.length()>0) {
            if ((anchorIndex=Arrays.binarySearch(files, new File(anchorPath)))>=0&&anchorIndex<length){
                Debug.D("DDDDDDDDD "+anchorIndex);
                anchorIndex+=shift;//Make shift to ignore anchor
            }
            Debug.D("DDDDDd  DDDD "+anchorIndex);
        }
        if (anchorIndex<0||anchorIndex>=length){
            Debug.W("Can't load local folder while anchor index invalid."+anchorIndex+" "+length+" "+anchorPath);
            notifyFinish(Code.CODE_FAIL,"Anchor index invalid",browseFolder,callback);
            return null;
        }
        Debug.D("Browse local folder from "+anchorIndex+" with size "+limit);
        int size=Math.abs(limit);
        final LinkedList<Path> list=new LinkedList<>();
        File child=null;Path childPath=null;
        browseFolder.setFrom(anchorIndex);
        for (;anchorIndex < length&&anchorIndex>=0&&size>0; anchorIndex+=shift) {
            if (null==(child=files[anchorIndex])||null==(childPath=new LocalPath().apply(child))){
                Debug.D("Skip load local file which create path invalid."+child);
                continue;
            }else if (shift>0){
                list.add(childPath);
            }else{
                list.add(0,childPath);
            }
            --size;
        }
        browseFolder.setData(list);
        notifyFinish(Code.CODE_SUCCEED,null,browseFolder,callback);
        return (cancel)->false;
    }
}
