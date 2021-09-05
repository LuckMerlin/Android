package com.merlin.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.Progress;
import luckmerlin.task.Result;
import luckmerlin.task.StreamTask;
import luckmerlin.task.TaskResult;

public class FileCopyTask extends StreamTask {
    private final Path mFrom;
    private final Path mTo;

    public FileCopyTask(Path from, Path to) {
        this(from,to,STATUS_IDLE,null,null);
    }

    public FileCopyTask(Path from, Path to, int status, Result result, Progress progress) {
        super(status, result, progress);
        mFrom=from;
        mTo=to;
    }

    @Override
    protected TaskInputStream openInputStream(Updater<TaskResult> updater) {
        Path from=mFrom;
        String fromPath=null!=from?from.getPath():null;
        if (null==fromPath||fromPath.length()<=0){
            Debug.W("Can't open input stream while to path invalid.");
            return null;
        }else if(from.isLocal()){
            File file=new File(fromPath);
            try {
                if (!file.exists()){
                    Debug.TW("Fail open local file input stream while file not exist.",file);
                    return null;
                }else if (!file.canRead()){
                    Debug.TW("Fail open file input stream while file none permission.",file);
                    return null;
                }
                FileInputStream inputStream=new FileInputStream(file);
                updater.finishCleaner(true,(TaskResult result)-> {
                    close(inputStream);
                    Debug.TD("Closed local file input stream.",file);
                });
                Debug.TD("Opened local file input stream.",file);
                return new TaskInputStream(inputStream,file.length());
            } catch (Exception e) {
                Debug.E("Fail local file task input stream while exception."+e,e);
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected TaskOutputStream openOutputSteam(Updater<TaskResult> updater) {
        Path to=mTo;
        String toPath=null!=to?to.getPath():null;
        if (null==toPath||toPath.length()<=0){
            Debug.W("Can't open local output stream while to path invalid.");
            return null;
        }else if(to.isLocal()){
            File file=new File(toPath);
            try {
                if (!file.exists()){
                    File parent=file.getParentFile();
                    if (null!=parent&&!parent.exists()){
                        Debug.TW("Created local file parent while open task stream.",file);
                        parent.mkdirs();
                    }
                    if (file.createNewFile()){
                        Debug.TW("Created local file while open task stream.",file);
                        updater.finishCleaner(true,(result)-> {
                            if (null==result||!result.isSucceed()){
                                Debug.W("Deleting created local file after task fail.");
                                file.delete();
                            }
                        });
                    }
                }
                if (!file.exists()){
                    Debug.TW("Fail open local file output stream while file not exist.",file);
                    return null;
                }else if (!file.canWrite()){
                    Debug.TW("Fail open local file output stream while file none permission.",file);
                    return null;
                }
                FileOutputStream outputStream=new FileOutputStream(file,true);
                updater.finishCleaner(true,(TaskResult result)-> {
                    close(outputStream);
                    Debug.TD("Closed local file output stream.",file);
                });
                Debug.TD("Opened local file output stream.",file);
                return new TaskOutputStream(outputStream,file.length());
            } catch (Exception e) {
                Debug.E("Fail open local file output stream while exception."+e,e);
                e.printStackTrace();
            }
            return null;
        }
        String host=to.getHost();
        if (null==host||host.length()<=0){
            Debug.W("Can't open remote output stream while to host invalid.");
            return null;
        }
        HttpURLConnection connection=openHttpConnection(host,"POST");
        if (null==connection){
            Debug.W("Fail open remote output stream while connect invalid.");
            return null;
        }
        try {
            Debug.TD("To open remote output stream.",host);
            inflateHeader(connection,Label.LABEL_PATH,toPath);
            connection.connect();
            String contentType=connection.getContentType();
            String connHeaderLength = connection.getHeaderField("content-length");
            Debug.D("DDDDDDDDD "+contentType);
        } catch (IOException e) {
            Debug.E("Exception open remote output stream.e="+e);
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection openHttpConnection(String urlPath,String method) {
        URL url= null;
        try {
            url = null!=urlPath&&urlPath.length()>0?new URL(urlPath):null;
            HttpURLConnection conn = null!=url?(HttpURLConnection) url.openConnection():null;
            if (null!=conn){
                conn.setRequestMethod(null!=method&&method.length()>0?method:"GET");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setUseCaches(false);
                return conn;
            }
        } catch (Exception e) {
            Debug.E("Exception open http connect.e="+e,e);
            e.printStackTrace();
        }
        return null;
    }

    protected final boolean inflateHeader(HttpURLConnection connection,String key,String value){
        if (null!=connection&&null!=key&&key.length()>0&&null!=value){
            try {
                connection.setRequestProperty(key, URLEncoder.encode(value,"utf-8"));
                return true;
            } catch (UnsupportedEncodingException e) {
                Debug.E("Exception set value to header."+e,e);
                e.printStackTrace();
            }
        }
        return false;
    }

}
