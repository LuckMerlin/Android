package com.merlin.file;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.Convertor;
import luckmerlin.core.OnFinish;
import luckmerlin.core.Reply;
import luckmerlin.core.data.OnPageLoadFinish;
import luckmerlin.core.data.Page;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.json.Json;
import luckmerlin.core.json.JsonParser;
import merlin.file.adapter.Query;
import merlin.file.nas.NasHttp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NasClient extends Client<Query,Path>{
    private final NasHttp mNasHttp=new NasHttp();
    private final JsonParser mJsonParser=new JsonParser();
    private final ExecutorService mExecutors=Executors.newCachedThreadPool();

    public NasClient(String host,String name) {
        super(host,name);
    }

    public final Canceler execute(Runnable runnable){
        ExecutorService executor=mExecutors;
        if (null==executor||null==runnable){
            return null;
        }
        Future future= executor.submit(runnable);
        return null!=future?(interrupt)->future.cancel(interrupt):null;
    }

    @Override
    public Reply scan(Path path) {
        return null;
    }

    @Override
    public Canceler rename(Path path, String newName, OnFinish<Path> callback) {
        final String pathValue=null!=path?path.getPath():null;
        if (null==pathValue||pathValue.length()<=0){
            Debug.W("Can't rename nas file while path invalid.");
            notifyFinish(Code.CODE_ARGS,"Path invalid.",null,callback);
        }else if (null==(newName=(null!=newName?newName.trim():null))||newName.length()<=0){
            Debug.W("Can't rename nas file while new name invalid.");
            notifyFinish(Code.CODE_ARGS,"New name invalid.",null,callback);
            return null;
        }
        final String host=path.getHost();
        final NasHttp nasHttp=mNasHttp;
        if (null==host||host.length()<=0){
            Debug.W("Can't rename nas file while host invalid.");
            notifyFinish(Code.CODE_ARGS,"Host invalid.",null,callback);
            return null;
        }else if (null==nasHttp){
            Debug.W("Can't rename nas file while nat http invalid.");
            notifyFinish(Code.CODE_ARGS,"Nas http invalid.",null,callback);
            return null;
        }
        Debug.D("To rename nas path."+newName,pathValue);
        Json bodyJson=new Json().putSafe(Label.LABEL_PATH,pathValue).putSafe(Label.LABEL_NAME,newName);
        return executeHttp(host,"/file/rename","put",bodyJson, new ReplyCallback((Object json)-> {
                Convertor<Object, NasPath> nasPathParser=(Object json1)-> null!=json1&&
                            json1 instanceof JSONObject?new NasPath((JSONObject)json1):null;
                    return mJsonParser.object(nasPathParser,json);
                },callback));
    }

    @Override
    public int setHome(Context context, Path path) {
        return 0;
    }

    @Override
    public int open(Context context, Path path) {
        return 0;
    }

    @Override
    public Path getHome(Context context) {
        return null;
    }

    @Override
    public Canceler onLoad(Query folder, Path anchor, int limit, OnPageLoadFinish<Path> callback) {
        final String folderPath=null!=folder?folder.getPath():null;
        final String serverUrl=getHost();
        Debug.D("Browse nas folder."+folderPath);
        Json bodyData=new Json().putSafe(Label.LABEL_PATH,folderPath).
        putSafe(Label.LABEL_LIMIT,limit).putSafe(Label.LABEL_REVERSE,false);
        return executeHttp(serverUrl,"/file/browse","get",bodyData,
                new ReplyCallback<Page<Path>>((Object json)-> {
                    JSONObject folderJson=null!=json&&json instanceof JSONObject?(JSONObject)json:null;
                    Convertor<Object, NasPath> nasPathParser=(Object json1)-> null!=json1&&
                            json1 instanceof JSONObject?new NasPath((JSONObject)json1):null;
                    Folder responseFolder=new Folder(mJsonParser.object(nasPathParser,
                            folderJson.optJSONObject(Label.LABEL_PATH)),
                            folderJson.optLong(Label.LABEL_FROM),
                            folderJson.optLong(Label.LABEL_TOTAL));
                    responseFolder.setData(mJsonParser.list(nasPathParser,folderJson.optJSONArray(Label.LABEL_DATA)));
                        return responseFolder;
                },callback));
    }

    private <T>Canceler executeHttp(String serverUrl,String routeName,String method, Object body, ReplyCallback<T> callback){
        if (null==serverUrl||serverUrl.length()<=0){
            Debug.W("Can't execute http while server url invalid.");
            notifyFinish(Code.CODE_FAIL,null,null,callback);
            return null;
        }else if (null==method||method.length()<=0){
            Debug.W("Can't execute http while method invalid.");
            notifyFinish(Code.CODE_FAIL,null,null,callback);
            return null;
        }
        Request.Builder builder=null==serverUrl?null:new Request.Builder().
                url(null!=routeName&&routeName.length()>0?serverUrl+routeName:serverUrl);
        if (null==builder){
            Debug.W("Can't execute http while builder invalid.");
            notifyFinish(Code.CODE_FAIL,null,null,callback);
            return null;
        }
        RequestBody requestBody=null;
        if (null==body){
            //Do nothing
        }else if (body instanceof JSONObject){
            String string=body.toString();
            requestBody=RequestBody.create(null!=string?string:"",MediaType.get("application/json"));
        }else{
            Debug.W("Can't execute http while body NOT support.");
            notifyFinish(Code.CODE_FAIL,null,null,callback);
            return null;
        }
        Request request=builder.method(null!=method&&method.length()>0?method:"get",requestBody).build();
        OkHttpClient client=new OkHttpClient();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody=null!=response&&response.isSuccessful()?response.body():null;
                String content=null!=responseBody?responseBody.string():null;
                notifyFinish(null!=content?Code.CODE_SUCCEED:Code.CODE_FAIL,null,content,callback);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                notifyFinish(Code.CODE_FAIL,null,null,callback);
            }
        });
        return (cancel)->{
            if (null!=call&&!call.isCanceled()){
                call.cancel();
                return true;
            }
            return false;
        };
    }

    private static final class ReplyCallback<T> implements OnFinish<String>{
        private final Convertor<Object,T> mParser;
        private final OnFinish<T> mOnFinish;

        ReplyCallback(Convertor<Object,T> parser,OnFinish<T> callback){
            mParser=parser;
            mOnFinish=callback;
        }

        @Override
       final public void onFinish(int code, String note, String data) {
            T responseData=null;
            if (null!=data&&data.length()>0){
                Convertor<Object,T> parser=mParser;
                try {
                    Json json=null!=parser&&null!=data&&data.length()>0?Json.create(data):null;
                    if (null!=json){
                        code=json.getInt(Label.LABEL_CODE);
                        note=json.optString(Label.LABEL_MESSAGE);
                        responseData=parser.convert(json.opt(Label.LABEL_DATA));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            finish(code,note,responseData,mOnFinish);
        }
    }
}
