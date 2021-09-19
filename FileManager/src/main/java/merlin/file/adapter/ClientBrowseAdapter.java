package merlin.file.adapter;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.ItemBrowsePathBinding;
import com.file.manager.databinding.ListEmptyBinding;
import com.merlin.file.Client;
import com.merlin.file.Mode;
import com.merlin.file.Path;
import java.util.List;

import luckmerlin.core.Code;
import luckmerlin.core.data.Pager;
import luckmerlin.core.debug.Debug;
import luckmerlin.databinding.Binding;
import luckmerlin.databinding.touch.Click;
import luckmerlin.databinding.view.Image;
import luckmerlin.databinding.view.Text;
import merlin.file.util.ThumbResources;

public class ClientBrowseAdapter extends PageListAdapter<Query, Path>{
    private final ThumbResources mThumbs=new ThumbResources();
    private Mode mMode;

    @Override
    protected final Integer onResolveViewTypeLayoutId(ViewGroup parent,int viewType) {
        switch (viewType){
            case TYPE_DATA:
                return R.layout.item_browse_path;
            case TYPE_EMPTY:
                return R.layout.list_empty;
        }
        return null;
    }

    public final boolean setMode(Mode mode){
        Mode current=mMode;
        if ((null==current&&null==mode)||(null!=mode&&null!=current&&current==mode)){
            return false;
        }
        mMode=mode;
        notifyVisibleDataChanged();
        return false;
    }

    public final boolean browse(String path){
        return null!=path&&null!=reset(new Query().setPath(path));
    }

    public final Client getClient(){
        Pager pager=getPager();
        return null!=pager&&pager instanceof Client?(Client)pager:null;
    }

    @Override
    protected Query onPreResetLoad(Query arg) {
        Client client=getClient();
        if (null==arg&&null!=client){
            Path path=client.getHome(getAdapterContext());
            String pathValue=null!=path?path.getPath():null;
            return null!=pathValue?new Query().setPath(pathValue):null;
        }
        return arg;
    }

    @Override
    protected final void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Path data, List<Object> payloads) {
        if (null!=binding){
            if (binding instanceof ItemBrowsePathBinding){
                ItemBrowsePathBinding itemBinding=(ItemBrowsePathBinding)binding;
                Object thumbObject=null!=data?data.isDirectory()?R.drawable.icon_folder:mThumbs.getThumb(data.getPath()):null;
                thumbObject=null!=thumbObject||null==data?thumbObject:mThumbs.getMimeTypeThumb(data.getMimeType());
                itemBinding.setThumb(null!=thumbObject?Image.image(thumbObject):null);
                Mode mode=mMode;boolean multiChoose=false;boolean chosen=false;
                if (null!=mode&&mode.getMode()==Mode.MODE_MULTI_CHOOSE){
                    multiChoose=true;chosen=mode.contains(data);
                }
                itemBinding.setChoosed(chosen);
                itemBinding.setMultiChoose(multiChoose);
                itemBinding.setPath(data);
            }else if (binding instanceof ListEmptyBinding){
                ListEmptyBinding emptyBinding=(ListEmptyBinding)binding;
                String title=getText(R.string.empty);
                Drawable iconDrawable=getDrawable(R.drawable.icon_empty);
                Binding clickBinding=Text.text(R.string.reset).add(Click.c(R.string.reset));
                if (isLatestCode(Code.CODE_NONE_ACCESS)){
                    title=getText(R.string.none_permission);
                    iconDrawable=getDrawable(R.drawable.icon_none_permission);
                }
                emptyBinding.setIconDrawable(iconDrawable);
                emptyBinding.setTitle(title);
                emptyBinding.setClick(clickBinding);
            }
        }
    }
}
