package com.merlin.file;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.data.OnPageLoadFinish;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.json.Json;
import luckmerlin.core.json.JsonParser;
import luckmerlin.core.json.Parser;
import merlin.file.adapter.Query;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NasClient extends Client<Query,Path>{
    private final String mServerUrl;

    public NasClient(String name,String serverUrl) {
        super(name);
        mServerUrl=serverUrl;
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
        final String serverUrl=mServerUrl;
        Debug.D("Browse nas folder."+folderPath);
        Request.Builder builder=requestBuilder(serverUrl,"/file/browse");
        if (null==builder){
            Debug.W("Can't load nas folder while api invalid.");
            notifyFinish(Code.CODE_FAIL,"Api invalid",null,callback);
            return null;
        }
        Json bodyData=new Json().putSafe(Label.LABEL_PATH,folderPath).
                putSafe(Label.LABEL_LIMIT,limit).putSafe(Label.LABEL_REVERSE,false);
        String bodyText=bodyData.toString();
        RequestBody requestBody=RequestBody.create(null!=bodyText?bodyText:"",MediaType.get("application/json"));
        Request request=builder.method("get",requestBody).build();
        OkHttpClient client=new OkHttpClient();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call,IOException e) {
                Debug.W("Fail load nas folder while exception.e="+e);
                notifyFinish(Code.CODE_EXCEPTION,"Exception "+e,null,callback);
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException {
                ResponseBody responseBody=null!=response&&response.isSuccessful()?response.body():null;
                String content=null!=responseBody?responseBody.string():null;
                JSONObject responseJsonObject=null!=content?Json.create(content):null;
                final Integer code=null!=responseJsonObject?responseJsonObject.optInt(Label.LABEL_CODE):null;
                if (null==responseJsonObject||null==code){
                    Debug.TW("Fail load nas folder while response invalid.",content);
                    notifyFinish(Code.CODE_EXCEPTION,"Response invalid",null,callback);
                    return;
                }
                String msg=responseJsonObject.optString(Label.LABEL_MESSAGE);
                JSONObject folderJson=responseJsonObject.optJSONObject(Label.LABEL_DATA);
                Folder responseFolder=null;
                if (null!=folderJson){
                    Parser<NasPath> parser=(int index,Object json)-> null!=json&&json instanceof JSONObject ?
                                new NasPath((JSONObject)json):null;
                    JsonParser jsonParser=new JsonParser();
                    responseFolder=new Folder(jsonParser.object(parser,
                            folderJson.optJSONObject(Label.LABEL_PATH)),
                            folderJson.optLong(Label.LABEL_FROM),
                            folderJson.optLong(Label.LABEL_TOTAL));
                    responseFolder.setData(jsonParser.list(parser,folderJson.optJSONArray(Label.LABEL_DATA)));
                }
                Debug.TD("Finish load nas folder.",responseFolder);
                notifyFinish(code,msg,responseFolder,callback);
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

    private Request.Builder requestBuilder(String serverUrl,String route){
        return null==serverUrl?null:new Request.Builder().url(null!=route?serverUrl+route:serverUrl);
    }
}
