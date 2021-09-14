package merlin.file.model;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.AlertMessageBinding;
import com.file.manager.databinding.BrowserMenusBinding;
import com.file.manager.databinding.PathContextMenusBinding;
import com.merlin.file.Client;
import com.merlin.file.Folder;
import com.merlin.file.LocalClient;
import com.merlin.file.Mode;
import com.merlin.file.NasClient;
import com.merlin.file.Path;
import com.merlin.file.TaskActivity;
import com.merlin.file.TaskService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import luckmerlin.core.Code;
import luckmerlin.core.OnFinishSucceed;
import luckmerlin.core.Result;
import luckmerlin.core.data.Page;
import luckmerlin.core.debug.Debug;
import luckmerlin.core.service.ServiceConnector;
import luckmerlin.databinding.model.M;
import luckmerlin.databinding.OnModelBind;
import luckmerlin.databinding.model.OnActivityStarted;
import luckmerlin.databinding.model.OnActivityStoped;
import luckmerlin.databinding.touch.OnViewClick;
import luckmerlin.databinding.touch.OnViewLongClick;
import luckmerlin.task.OnTaskUpdate;
import luckmerlin.task.Progress;
import luckmerlin.task.Status;
import luckmerlin.task.Task;
import luckmerlin.task.TaskBinder;
import merlin.file.adapter.ClientBrowseAdapter;
import merlin.file.adapter.Query;
import merlin.file.task.CopyTask;
import merlin.file.task.DeleteTask;
import merlin.file.task.DownloadTask;
import merlin.file.task.MoveTask;
import merlin.file.task.PathTaskCreator;
import merlin.file.task.UploadTask;
import merlin.file.util.FileSize;

public class BrowserActivityModel extends BaseModel implements OnViewClick,
        OnViewLongClick, OnTaskUpdate, OnActivityStarted, OnActivityStoped {
    private final ObservableField<Client> mCurrentClient=new ObservableField<>();
    private final ObservableField<Mode> mCurrentMode=new ObservableField<>(new Mode(Mode.MODE_NORMAL));
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mClientCount=new ObservableField<>();
    private final ObservableField<String> mNotifyText=new ObservableField<>();
    private final PopupWindow mPopupWindow=new PopupWindow();
    private final ServiceConnector mConnector=new ServiceConnector();
    private Runnable mNotifyDelayRunnable;
    private final ObservableField<RecyclerView.Adapter> mContentAdapter=new ObservableField<>();
    private final ClientBrowseAdapter mBrowserAdapter=new ClientBrowseAdapter(){
        @Override
        protected void onPageLoadFinish(int code, String note, Page<Path> data, Query arg, Path anchor, int limit) {
            super.onPageLoadFinish(code, note, data, arg, anchor, limit);
            if ((code&Code.CODE_CANCEL)<=0){
                mCurrentFolder.set(null!=data&&data instanceof Folder?(Folder)data:null);
            }
        }
    };
    private List<Client> mClients;

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        addClient(new LocalClient(getText(R.string.local)));
        addClient(new NasClient("http://192.168.0.4:5000",getText(R.string.nas)));
        selectAny();
        mContentAdapter.set(mBrowserAdapter);
        entryMode(new Mode(Mode.MODE_NORMAL));
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (isCurrentActivity(activity)){
            bindService(TaskService.class,mConnector.setConnect((ComponentName name, IBinder service)-> {
                if (null!=service&&service instanceof TaskBinder){
                    TaskBinder binder=((TaskBinder)service);
                    binder.put(BrowserActivityModel.this,null);
                }
            }), Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onTaskUpdate(int status, Task task, Object arg) {
        String taskName=null;
        if (null!=task){
            status=task.getStatus();
            taskName=task.getName();
        }
        String statusName=null;
        switch (status){
            case Status.STATUS_IDLE:
                Result result=null!=task?task.getResult():null;
                statusName="【"+(null!=result?getText(result.isSucceed()?
                        R.string.succeed:R.string.failed):getText(R.string.idle))+"】";
                break;
            case Status.STATUS_DOING:
                Progress progress=null!=task?task.getProgress():null;
                if (null==progress){
                    statusName="【"+getText(R.string.doing)+"】";
                }else{
                    statusName="【"+progress.getProgress()+"】"+
                            FileSize.formatSizeText(progress.getSpeed())+"/s";
                }
                break;
            case Status.STATUS_PREPARE: statusName="【"+getText(R.string.prepare)+"】";break;
            case Status.STATUS_RECHECK: statusName="【"+getText(R.string.recheck)+"】";break;
            case Status.STATUS_WAIT: statusName="【"+getText(R.string.wait)+"】";break;
            default:statusName="【"+getText(R.string.unknown)+"】";break;
        }
        resetNotifyText((null!=taskName?taskName:"")+statusName);
    }

    @Override
    public boolean onLongClicked(int viewId, View view, Object tag) {
        switch (viewId){
            case R.layout.item_browse_path:
                return showPathContextMenu(view,null!=tag&&tag instanceof Path?(Path)tag:null)||true;
        }
        return false;
    }

    private boolean showBrowserContextMenu(View view){
        if (null!=view){
            mPopupWindow.dismiss();
            ViewDataBinding binding=M.setContentView(view.getContext(),
                    mPopupWindow,R.layout.browser_menus,null);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.showAtLocation(view,Gravity.CENTER,0,0);
            if (null==binding||!(binding instanceof BrowserMenusBinding)){
                mPopupWindow.dismiss();
                return false;
            }
            BrowserMenusBinding menusBinding=(BrowserMenusBinding)binding;
            menusBinding.setFolder(mCurrentFolder.get());
            return true;
        }
        return false;
    }

    private boolean showPathContextMenu(View view,Path path){
        if (null!=view&&null!=path){
            mPopupWindow.dismiss();
            ViewDataBinding binding=M.setContentView(view.getContext(),
                    mPopupWindow,R.layout.path_context_menus,null);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.showAtLocation(view,Gravity.CENTER,0,0);
            if (null==binding||!(binding instanceof PathContextMenusBinding)){
                mPopupWindow.dismiss();
                return false;
            }
            ((PathContextMenusBinding)binding).setPath(path);
            return true;
        }
        return false;
    }

    public boolean addClient(Client client){
        if (null!=client){
            List<Client> clients=mClients;
            if (!(null!=clients?clients:(clients=mClients=new ArrayList<>())).contains(client)){
                clients.add(client);
                mClientCount.set(clients.size());
                return true;
            }
        }
        return false;
    }

    private Client nextClient(Client client){
        List<Client> clients=mClients;
        int length=null!=clients?clients.size():-1;
        if (length>0){
            int index=(null!=client?clients.indexOf(client):-1)+1;
            return clients.get(index>=0&&index<length?index:0);
        }
        return null;
    }

    @Override
    public boolean onClicked(View view, int id, int count, Object tag) {
        mPopupWindow.dismiss();
        switch (id){
            case R.layout.item_browse_path:
                if (null!=tag&&tag instanceof Path){
                    Path path=(Path)tag;
                    Mode mode=mCurrentMode.get();//Check fot multi choose
                    if (null!=mode&&mode.getMode()==Mode.MODE_MULTI_CHOOSE){
                        return (mode.toggle(path)&&mBrowserAdapter.notifyChildChanged(path)>=0)||true;
                    }
                    return openPath(path)||true;
                }
                return true;
            case R.drawable.selector_back: return backward()||true;
            case R.layout.device_text:
                if (null!=tag&&tag instanceof Client){
                    Client client=nextClient((Client)tag);
                    if (null!=client){
                        return select(client)||true;
                    }
                }
                return true;
            case R.string.rename: renamePath(view,null!=tag&&tag instanceof Path?(Path)tag:null);break;
            case R.drawable.selector_menu: return showBrowserContextMenu(view)||true;
            case R.string.multiChoose: return entryMode(new Mode(Mode.MODE_MULTI_CHOOSE))||true;
            case R.string.transporter: return startActivity(TaskActivity.class)||true;
            case R.drawable.selector_cancel: return entryMode(null)||true;
//            case R.string.sure: executeIfNotEmpty((paths)->new ChooseTask(paths),true);break;
            case R.string.upload: entryOrExecuteIfNotEmpty(Mode.MODE_UPLOAD,tag,
                    (paths,folder)->new UploadTask(paths,folder),true);break;
            case R.string.download: entryOrExecuteIfNotEmpty(Mode.MODE_DOWNLOAD,tag,
                    (paths,folder)->new DownloadTask(paths,folder),true);break;
            case R.string.move: entryOrExecuteIfNotEmpty(Mode.MODE_MOVE,tag,
                    (paths,folder)->new MoveTask(paths,folder),true);break;
            case R.string.copy: entryOrExecuteIfNotEmpty(Mode.MODE_COPY,tag,
                    (paths,folder)->new CopyTask(paths,folder),true);break;
            case R.string.delete: entryOrExecuteIfNotEmpty(null,tag,
                    (paths,folder)->new DeleteTask(paths), true);break;
        }
        return false;
    }

    private boolean entryOrExecuteIfNotEmpty(Integer mode,Object selectObject,
                                             PathTaskCreator creator, boolean emptyNotify){
        if (null!=mode&&mode==Mode.MODE_MULTI_CHOOSE){
            return false;//Invalid
        }
        final Mode currentMode=mCurrentMode.get();
        List<Path> paths=null;
        if (null!=currentMode){
            if (null!=selectObject&&selectObject instanceof Path&&
                    !currentMode.contains(selectObject)){
                currentMode.add((Path)selectObject);
            }
            paths=currentMode.getList();
        }
        if (null==paths||paths.size()<=0){
            return emptyNotify?toast(getText(R.string.whichEmpty,getText(R.string.choose)))||true:true;
        }
        int current=currentMode.getMode();
        Folder currentFolder=mCurrentFolder.get();
        if (null!=mode&&(current==Mode.MODE_MULTI_CHOOSE||current==Mode.MODE_NORMAL)){
            return entryMode(new Mode(mode,paths));
        }else if (current==Mode.MODE_UPLOAD&&null!=currentFolder&&currentFolder.isLocal()){
            return toast(R.string.canNotInCurrent)&&false;
        }else if (current==Mode.MODE_DOWNLOAD&&null!=currentFolder&&!currentFolder.isLocal()){
            return toast(R.string.canNotInCurrent)&&false;
        }
        Task task=null!=creator?creator.create(paths,currentFolder):null;
        return null!=task&&startTask(task)&&entryMode(null);
    }

    private boolean startTask(Task task) {
        ServiceConnector connector=null!=task?mConnector:null;
        TaskBinder taskBinder=null!=connector?connector.getBinder(TaskBinder.class):null;
        if (null==taskBinder){
            Debug.W("Can't start task while binder invalid.");
            return false;
        }
        return taskBinder.start(task)!=null;
    }

    private boolean resetNotifyText(String text){
        String current=mNotifyText.get();
        if ((null==current&&null==text)||(null!=current&&null!=text&&current.equals(text))){
            return false;
        }
        mNotifyText.set(text);
        return post(mNotifyDelayRunnable=new Runnable() {
            @Override
            public void run() {
                Runnable runnable=mNotifyDelayRunnable;
                if (null!=runnable&&runnable==this){
                    mNotifyDelayRunnable=null;
                    mNotifyText.set(null);
                }
            }
        },3000);
    }

    private boolean renamePath(View view,Path path){
        final Client client=mCurrentClient.get();
        if (null==view||null==path){
            return false;
        }else if(null==client){
            return toast(getText(R.string.failed))&&false;
        }
        String hint=path.getName();
        hint=null!=hint&&hint.length()>0?hint:getText(R.string.inputPlease);
        PopupWindow popupWindow=new PopupWindow();
        return showAlertMessage(popupWindow,new AlertMessageModel(){
            @Override
            public boolean onClicked(View view, int id, int count, Object tag) {
                if (id==R.string.sure){
                    String inputText=getInputText();
                    if (null==inputText||inputText.length()<=0){
                        return toast(R.string.rename,getText(R.string.input))||true;
                    }
                    client.rename(path, inputText,(OnFinishSucceed<Path>)
                            (int code, String note, Path data)-> mBrowserAdapter.replace(path,data));
                }
                popupWindow.dismiss();
                return super.onClicked(view, id, count, tag);
            }
        }.setTitle(getText(R.string.rename)).setInputHit(hint).setLeft(R.string.sure).setRight(R.string.cancel));
    }

    private boolean showAlertMessage(PopupWindow popupWindow,AlertMessageModel messageModel){
        ViewDataBinding binding=null;
        if (null!=popupWindow&&null!=messageModel&&null!=(binding=M.setContentView(getContext(),
            popupWindow, R.layout.alert_message, (OnModelBind)(ViewDataBinding binding1)-> {
                if (null!=binding1&&binding1 instanceof AlertMessageBinding){
                    ((AlertMessageBinding)binding1).setVm(messageModel);
                    return messageModel;
                }
                return null;
            }))){
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(binding.getRoot(),Gravity.CENTER,0,0);
            return true;
        }
        return false;
    }

    private boolean entryMode(Mode mode){
        Mode current=mCurrentMode.get();
        if ((null==current&&null==mode)||(null!=current&&null!=mode&&current.getMode()==mode.getMode())){
            return false;
        }
        mCurrentMode.set(mode=null!=mode?mode:new Mode(Mode.MODE_NORMAL));
        mBrowserAdapter.setMode(mode);
        return true;
    }

    private boolean backward(){
        Folder folder=mCurrentFolder.get();
        String parent=null!=folder?folder.getParent():null;
        return null!=parent&&browsePath(parent);
    }

    private boolean browsePath(String path){
        ClientBrowseAdapter adapter=null!=path?mBrowserAdapter:null;
        return null!=adapter&&adapter.browse(path);
    }

    private boolean openPath(Path path){
        if (null==path){
            return false;
        }else if (path.isDirectory()){
            return browsePath(path.getPath());
        }
        Client client=mCurrentClient.get();
        return null!=client&&client.open(getContext(),path)==Code.CODE_SUCCEED;
    }

    public boolean removeClient(Client client){
        List<Client> clients=null!=client?mClients:null;
        if (null!=clients&&!clients.remove(client)){
            int size=clients.size();
            mClientCount.set(size);
            if (size<=0){
                mClients=null;
            }
            Client current=mCurrentClient.get();
            if (null!=current&&current.equals(client)){
                mCurrentClient.set(null);
                selectAny();
            }
            return true;
        }
        return false;
    }

    public boolean selectAny(){
        if (null==mCurrentClient.get()){
            List<Client> clients=mClients;
            int size=null!=clients?clients.size():0;
            return size>0&&select(new Random().nextInt(size));
        }
        return false;
    }

    public boolean select(Object clientObj){
        List<Client> clients=mClients;
        int size=null!=clientObj&&null!=clients?clients.size():-1;
        Client client=null;
        if (size<=0){
            client=null;
        }else if (clientObj instanceof Integer){
            Integer index=(Integer)clientObj;
            client=index>=0&&index<size?clients.get(index):null;
        }else{
            for (Client child:clients) {
                if (null!=child&&child.equals(clientObj)){
                    client=child;
                    break;
                }
            }
        }
        //
        Debug.TD("Select client.",client);
        ClientBrowseAdapter browseAdapter=mBrowserAdapter;
        if (null!=browseAdapter){
            browseAdapter.setPager(client);
        }
        mCurrentClient.set(client);
        return true;
    }

    public ObservableField<Client> getCurrentClient() {
        return mCurrentClient;
    }

    public ObservableField<Folder> getCurrentFolder() {
        return mCurrentFolder;
    }

    public ObservableField<Mode> getMode() {
        return mCurrentMode;
    }

    public ObservableField<Integer> getClientCount() {
        return mClientCount;
    }

    public ObservableField<RecyclerView.Adapter> getContentAdapter() {
        return mContentAdapter;
    }

    public ObservableField<String> getNotifyText() {
        return mNotifyText;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (isCurrentActivity(activity)){
            TaskBinder binder=mConnector.getBinder(TaskBinder.class);
            if (null!=binder){
                binder.remove(this);
            }
            unbindService(mConnector);
        }
    }

    @Override
    protected void onRootDetached(View view) {
        super.onRootDetached(view);
        TaskBinder binder=mConnector.getBinder(TaskBinder.class);
        if (null!=binder){
            binder.remove(this);
        }
        unbindService(mConnector);
    }

}
