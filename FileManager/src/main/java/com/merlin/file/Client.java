package com.merlin.file;

import android.os.Environment;
import luckmerlin.core.OnFinish;
import luckmerlin.core.data.Pager;

public abstract class Client<A,T extends Path> implements Pager<A,T> {
    private Path mHome;
    private final String mName;

    public Client(String name){
        mName=name;
    }

    public final Client setHome(Path path){
        mHome=path;
        return this;
    }

    public final String getName() {
        return mName;
    }

    public final Path getHome() {
        Path path=mHome;
        return null!=path?path:new LocalPath().apply(Environment.getRootDirectory());
    }

    protected final<G> boolean notifyFinish(int code, String note, G data, OnFinish<G> callback){
        if (null!=callback){
            callback.onFinish(code,note,data);
            return true;
        }
        return false;
    }
}
