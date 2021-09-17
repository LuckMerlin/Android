package com.merlin.file;

import android.content.Context;

import luckmerlin.core.Canceler;
import luckmerlin.core.OnFinish;
import luckmerlin.core.Reply;
import luckmerlin.core.data.Pager;

public abstract class Client<A,T extends Path> implements Pager<A,T> {
    private final String mName;
    private final String mHost;

    public Client(String host,String name){
        mName=name;
        mHost=host;
    }

    public final String getHost() {
        return mHost;
    }

    public abstract Canceler rename(Path path, String newName, OnFinish<Path> callback);

    public abstract Reply scan(Path path);

    public abstract int setHome(Context context, Path path);

    public abstract int open(Context context,Path path);

    public final String getName() {
        return mName;
    }
    public abstract Path getHome(Context context) ;

    protected final<G> boolean notifyFinish(int code, String note, G data, OnFinish<G> callback){
        if (null!=callback){
            callback.onFinish(code,note,data);
            return true;
        }
        return false;
    }
}
