package merlin.file.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.ItemTaskBinding;
import java.util.List;

import luckmerlin.core.Result;
import luckmerlin.core.debug.Debug;
import luckmerlin.task.Status;
import luckmerlin.task.Task;
import luckmerlin.task.TaskResult;
import merlin.file.task.DownloadTask;
import merlin.file.task.UploadTask;

public class TaskListAdapter extends ListAdapter<Task>{

    @Override
    protected Integer onResolveDataTypeLayoutId(ViewGroup parent) {
        return R.layout.item_task;
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
