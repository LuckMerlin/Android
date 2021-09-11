package luckmerlin.core.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServiceConnector implements ServiceConnection {
    private Map<ComponentName,IBinder> mBoundMap;
    private OnBindChange mConnection;

    public interface OnBindChange{
        void onBoundChanged(ComponentName name, IBinder service);
    }

    public final ServiceConnector setConnect(OnBindChange connect){
        mConnection=connect;
        return this;
    }

    @Override
    public final void onServiceConnected(ComponentName name, IBinder service) {
        if (null!=service){
            Map<ComponentName,IBinder> boundMap=mBoundMap;
            (null!=boundMap?boundMap:(mBoundMap=new HashMap<>())).put(name,service);
        }
        OnBindChange connection=mConnection;
        if (null!=connection){
            connection.onBoundChanged(name,service);
        }
    }

    @Override
    public final void onServiceDisconnected(ComponentName name) {
        Map<ComponentName,IBinder> boundMap=null!=name?mBoundMap:null;
        if (null!=boundMap){
            boundMap.remove(name);
        }
        OnBindChange connection=mConnection;
        if (null!=connection){
            connection.onBoundChanged(name,null);
        }
    }

    public final <T extends Binder> T getBinder(Class<T> cls) {
        Map<ComponentName,IBinder> map=null!=cls?mBoundMap:null;
        Collection<IBinder> binders=null!=map?map.values():null;
        if (null!=binders){
            for (IBinder child:binders) {
                if (null!=child&&cls.isAssignableFrom(child.getClass())){
                    return (T)child;
                }
            }
        }
        return null;
    }
}
