package merlin.file.adapter;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.ItemBrowsePathBinding;
import com.merlin.file.Client;
import com.merlin.file.Path;
import java.util.List;

import luckmerlin.core.data.Pager;
import luckmerlin.databinding.touch.Image;
import merlin.file.util.ThumbResources;

public class ClientBrowseAdapter extends PageListAdapter<Path, Path>{
    private final ThumbResources mThumbs=new ThumbResources();

    @Override
    protected final Integer onResolveViewTypeLayoutId(int viewType) {
        return R.layout.item_browse_path;
    }

    public final Client getClient(){
        Pager pager=getPager();
        return null!=pager&&pager instanceof Client?(Client)pager:null;
    }

    @Override
    protected Path onPreResetLoad(Path arg) {
        Client client=getClient();
        return null==arg&&null!=client?client.getHome(getAdapterContext()):arg;
    }

    @Override
    protected final void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Path data, List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemBrowsePathBinding){
            ItemBrowsePathBinding itemBinding=(ItemBrowsePathBinding)binding;
            Object thumbObject=null!=data?data.isDirectory()?R.drawable.icon_folder:mThumbs.getThumb(data.getPath()):null;
            thumbObject=null!=thumbObject||null==data?thumbObject:mThumbs.getMimeTypeThumb(data.getMimeType());
            itemBinding.setThumb(null!=thumbObject?Image.image(thumbObject):null);
            itemBinding.setPath(data);
        }
    }
}
