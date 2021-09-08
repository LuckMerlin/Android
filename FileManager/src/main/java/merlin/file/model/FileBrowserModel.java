package merlin.file.model;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.merlin.file.Client;
import com.merlin.file.FileCopyTask;
import com.merlin.file.Folder;
import com.merlin.file.LocalClient;
import com.merlin.file.LocalPath;
import com.merlin.file.Mode;
import com.merlin.file.NasClient;
import com.merlin.file.NasPath;
import com.merlin.file.Path;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import luckmerlin.core.Code;
import luckmerlin.core.data.Page;
import luckmerlin.core.debug.Debug;
import luckmerlin.databinding.touch.OnViewClick;
import luckmerlin.task.TaskExecutor;
import merlin.file.adapter.ClientBrowseAdapter;
import merlin.file.adapter.Query;
import merlin.file.test.TestNasFilePath;

public class FileBrowserModel extends BaseModel implements OnViewClick {
    private final ObservableField<Client> mCurrentClient=new ObservableField<>();
    private final ObservableField<Mode> mCurrentMode=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mClientCount=new ObservableField<>();
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
//        addClient(new LocalClient(getText(R.string.local)));
        addClient(new NasClient("http://192.168.0.4:5000",getText(R.string.nas)));
        selectAny();
        mContentAdapter.set(mBrowserAdapter);
        //
        TaskExecutor executor=new TaskExecutor();
//        executor.append(new FileCopyTask(new LocalPath().apply(new File("/sdcard/dddd.pdf")),
//                new LocalPath().apply(new File("/sdcard/lin.pdf"))));
//        executor.start();
        post(new Runnable() {
            @Override
            public void run() {
                Folder folder=mCurrentFolder.get();
//                null!=folder?folder.getpa
                executor.append(new FileCopyTask(
//                        new LocalPath().apply(
//                        new File("/sdcard/dddd.pdf")),
//                        new File("/sdcard/独家记忆.mp3")),
                        new TestNasFilePath(),
                        new LocalPath().apply(new File("/sdcard/test.mp3"))
                        ));
                executor.start();
            }
        },4000);
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
        switch (id){
            case R.layout.item_browse_path:
                return openPath(null!=tag&&tag instanceof Path?(Path)tag:null)||true;
            case R.drawable.selector_back:
                return backward()||true;
            case R.layout.device_text:
                if (null!=tag&&tag instanceof Client){
                    Client client=nextClient((Client)tag);
                    if (null!=client){
                        return select(client)||true;
                    }
                }
                return true;
        }
        return false;
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
}
