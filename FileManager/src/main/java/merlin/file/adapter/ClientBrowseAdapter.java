package merlin.file.adapter;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.ItemBrowsePathBinding;
import com.merlin.file.Client;
import com.merlin.file.Path;
import java.util.List;
import luckmerlin.core.debug.Debug;

public final class ClientBrowseAdapter extends PageListAdapter<Client<Object,Path>, Path>{

    public ClientBrowseAdapter(Client client){
        super.setPager(client);
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return R.layout.item_browse_path;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Path data, List<Object> payloads) {
        Debug.D("DDD "+position+" "+data
        );
        if (null!=binding&&binding instanceof ItemBrowsePathBinding){
            ItemBrowsePathBinding itemBinding=(ItemBrowsePathBinding)binding;
            itemBinding.setPath(data);
        }
    }
}
