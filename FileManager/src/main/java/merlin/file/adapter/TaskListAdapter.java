package merlin.file.adapter;

import android.view.ViewGroup;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.ItemTaskBinding;
import java.util.List;
import luckmerlin.core.Result;
import luckmerlin.task.Status;
import luckmerlin.task.Task;
import merlin.file.task.DownloadTask;
import merlin.file.task.UploadTask;

public class TaskListAdapter extends ListAdapter<Task>{

    @Override
    protected Integer onResolveViewTypeLayoutId(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_DATA:
                return R.layout.item_task;
            case TYPE_EMPTY:
                return R.layout.list_empty;
        }
        return null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Task data, List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemTaskBinding){
            ItemTaskBinding taskBinding=(ItemTaskBinding)binding;
            taskBinding.setTask(data);
            int statusIcon=R.drawable.icon_cancel;
            final int status=null!=data?data.getStatus():Status.STATUS_IDLE;
            if (status == Status.STATUS_IDLE){
                statusIcon=R.drawable.icon_failed;
                Result result=null!=data?data.getResult():null;
                if (null!=result&&result.isSucceed()){
                    statusIcon=R.drawable.icon_succeed;
                }else if (data instanceof DownloadTask){
                    statusIcon=R.drawable.icon_download;
                }else if (data instanceof UploadTask){
                    statusIcon=R.drawable.icon_upload;
                }
            }
            taskBinding.setStatusIcon(statusIcon);
        }
    }
}
