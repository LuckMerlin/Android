package com.merlin.file;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import luckmerlin.core.Code;
import luckmerlin.core.Reply;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.json.Json;
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
    protected Input onOpenInput(boolean checkMd5, Updater<TaskResult> updater)  throws Exception{
        Path from=mFrom;
        String fromPath=null!=from?from.getPath():null;
        if (null==fromPath||fromPath.length()<=0){
            Debug.W("Can't open input stream while to path invalid.");
            return null;
        }else if(from.isLocal()){//Open local input file
            File file=new File(fromPath);
            if (!file.exists()){
                Debug.TW("Fail open local file input stream while file not exist.",file);
                return null;
            }else if (!file.canRead()){
                Debug.TW("Fail open file input stream while file none permission.",file);
                return null;
            }
            Debug.TD("Opened local file input stream.",file);
            return new Input(file.length(),null,(long skip)-> {
                long length=file.length();
                if (skip>length){
                    Debug.W("Fail open local input stream while skip large than total length.length="+length+" "+skip);
                    return null;
                }
                FileInputStream inputStream=new FileInputStream(file);
                addFinishClose(updater,inputStream);
                inputStream.skip(skip);
                return inputStream;
            });
        }
        final String host=from.getHost();
        if (null==host||host.length()<=0){
            Debug.W("Can't open input stream while host invalid.");
            return null;
        }
        Debug.W("Can't open input while NOT support.");
        return null;
    }

    @Override
    protected Output onOpenOutput(long inputLength, Updater<TaskResult> updater) throws Exception{
        Path to=mTo;
        final String toPath=null!=to?to.getPath():null;
        if (null==toPath||toPath.length()<=0){
            Debug.W("Can't open local output stream while to path invalid.");
            return null;
        }else if(to.isLocal()){//Open local output file
            File file=new File(toPath);
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
            Debug.TD("Opened local file output stream.",file);
            return new Output(file.length(),null,(skip)->{
                final long length=file.length();
                if (skip==length){
                    FileOutputStream outputStream=new FileOutputStream(file,true);
                    addFinishClose(updater,outputStream);
                    return outputStream;
                }
                Debug.W("Fail open local output stream while length NOT match skip.length="+length+" "+skip);
                return null;
            });
        }
        final String host=to.getHost();
        if (null==host||host.length()<=0){
            Debug.W("Can't open output stream while host invalid.");
            return null;
        }
        Reply<NasPath> nasReply=fetchNasFile(host,toPath);
        int code=null!=nasReply?nasReply.getCode():Code.CODE_FAIL;
        if ((code&Code.CODE_NOT_EXIST)<=0&&(code&Code.CODE_SUCCEED)<=0){//If not exist
            Debug.W("Can't open output stream while fetch reply fail.");
            return null;
        }
        final NasPath nasPath=null!=nasReply?nasReply.getData():null;
        long length=null!=nasPath?nasPath.getLength():0;
        Debug.D("AAAlength AAAAAa  "+length);
        return new Output(length,null,(long skip)-> {
                if (skip==length){
                    HttpURLConnection connection=openHttpConnection(host,"POST");
                    if (null==connection){
                        return null;
                    }
                    Debug.D("DDDDDDDDDd  "+host);
                    inflateHeader(connection,Label.LABEL_PATH,toPath);
                    inflateHeader(connection,Label.LABEL_FROM,""+length);
                    inflateHeader(connection,Label.LABEL_TOTAL,""+inputLength);
                    inflateHeader(connection,"Content-Type", "application/octet-stream");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setChunkedStreamingMode(0);
                    connection.setRequestProperty("Accept", "application/json");
                    updater.finishCleaner(true,(result)->connection.disconnect());
                    connection.connect();
                    OutputStream outputStream=connection.getOutputStream();
                    return outputStream;
                }
                Debug.W("Fail open nas output stream while length NOT match skip.length="+length+" "+skip);
                return null;
        });
    }

    private Reply<NasPath> fetchNasFile(String host,String path){
        if (null==host||host.length()<=0){
            Debug.W("Fail fetch nas path while host invalid.");
            return null;
        }else if (null==path||path.length()<=0){
            Debug.W("Fail fetch nas path while path invalid.");
            return null;
        }
        final HttpURLConnection connection=openHttpConnection(host,"GET");
        if (null==connection){
            Debug.W("Fail fetch nas path while connect invalid.");
            return null;
        }
        inflateHeader(connection,Label.LABEL_PATH,path);
        connection.setDoInput(true);
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(false);
        InputStreamReader streamReader=null;BufferedReader inputStreamReader=null;
        try {
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            String encoding=connection.getContentEncoding();
            streamReader=null!=inputStream?new InputStreamReader(inputStream,
                    null!=encoding&&encoding.length()>0?encoding:"UTF-8"):null;
            inputStreamReader = null!=streamReader?new BufferedReader(streamReader):null;
            String contentText=null;
            if (null!=inputStreamReader){
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = inputStreamReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                contentText=stringBuffer.length()>0?stringBuffer.toString():null;
            }
            Json jsonObject=null!=contentText&&contentText.length()>0? Json.create(contentText):null;
            if (null==jsonObject){
                Debug.TD("Can't parse nas path response while NONE json response.",contentText);
                return new Reply<>(Code.CODE_FAIL,"None json response.",null);
            }
            JSONObject dataJson=jsonObject.optJSONObject(Label.LABEL_DATA);
            return new Reply(jsonObject.optInt(Label.LABEL_CODE,Code.CODE_FAIL),
                    jsonObject.optString(Label.LABEL_MESSAGE),null!=dataJson?new NasPath(dataJson):null);
        }catch (Exception e){
            Debug.E("Exception parse nas path response.e="+e,e);
            e.printStackTrace();
        }finally {
            close(inputStreamReader,streamReader);
            connection.disconnect();
        }
        Debug.D("Can't parse nas path response.");
        return new Reply<>(Code.CODE_FAIL,"Fail.",null);
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
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(5000);
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
