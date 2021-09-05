package merlin.file.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import com.merlin.file.TaskService;
import luckmerlin.core.debug.Debug;

public class TaskActivityModel extends BaseModel {
    private ServiceConnection mConnection;

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        Context context=getContext();
        if (null!=context){
            unBind(context);
            context.bindService(new Intent(context, TaskService.class), mConnection=new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            Debug.D("DDD onServiceConnected DDDDDDDDD  "+service);
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                            mConnection=null;
                        }},Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onRootDetached(View view) {
        super.onRootDetached(view);
        unBind(null!=view?view.getContext():null);
    }

    private boolean unBind(Context context){
        ServiceConnection connection=mConnection;
        if (null!=context&&null!=connection){
            mConnection=null;
            context.unbindService(connection);
            return true;
        }
        return false;
    }
}
