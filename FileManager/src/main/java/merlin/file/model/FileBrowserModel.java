package merlin.file.model;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.merlin.file.Client;
import com.merlin.file.Folder;
import com.merlin.file.LocalClient;
import com.merlin.file.Mode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import luckmerlin.core.debug.Debug;
import merlin.file.adapter.ClientBrowseAdapter;

public class FileBrowserModel extends BaseModel {
    private final ObservableField<Client> mCurrentClient=new ObservableField<>();
    private final ObservableField<Mode> mCurrentMode=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mClientCount=new ObservableField<>();
    private final ObservableField<RecyclerView.Adapter> mContentAdapter=new ObservableField<>();
    private List<Client> mClients;

    @Override
    protected void onRootAttached() {
        super.onRootAttached();
        addClient(new LocalClient(getText(R.string.local)));
        selectAny();
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
        RecyclerView.Adapter adapter=mContentAdapter.get();
        if (null==adapter){
            mContentAdapter.set(new ClientBrowseAdapter(client));
        }else if (adapter instanceof ClientBrowseAdapter){
            ((ClientBrowseAdapter)adapter).setPager(client);
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
