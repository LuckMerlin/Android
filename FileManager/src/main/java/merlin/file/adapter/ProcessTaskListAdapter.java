package merlin.file.adapter;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.ItemTaskBinding;
import com.file.manager.databinding.ListEmptyBinding;
import java.util.List;
import luckmerlin.core.Result;
import luckmerlin.task.Progress;
import luckmerlin.task.Status;
import luckmerlin.task.Task;
import luckmerlin.task.Tasked;
import merlin.file.task.DownloadTask;
import merlin.file.task.UploadTask;

public class ProcessTaskListAdapter extends AbstractTaskListAdapter<Tasked>{

    @Override
    protected Integer onResolveViewTypeLayoutId(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_EMPTY:
                return R.layout.list_empty;
            case TYPE_DATA:
                return R.layout.item_task;
        }
        return null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType,
                                    ViewDataBinding binding, int position, Tasked data, List<Object> payloads) {
        if (null!=binding) {
            if (binding instanceof ItemTaskBinding) {
                ItemTaskBinding taskBinding = (ItemTaskBinding) binding;
                taskBinding.setTask(data);
                int status = Status.STATUS_IDLE;
                Result result = null;
                Progress progress = null;
                Task task=null;
                if (null != data) {
                    task=data.getTask();
                    status = data.getStatus();
                    result = data.getResult();
                    progress = data.getProgress();
                }
                String statusText=null;
                int statusIcon = R.drawable.icon_cancel;
                switch (status){
                    case Status.STATUS_WAIT:statusText = getText(R.string.wait);break;
                    case Status.STATUS_ADD: statusText = getText(R.string.add);break;
                    case Status.STATUS_DOING: statusText = getText(R.string.doing);break;
                    case Status.STATUS_PREPARE: statusText = getText(R.string.prepare);break;
                    case Status.STATUS_RECHECK: statusText = getText(R.string.recheck);break;
                    case Status.STATUS_REMOVE: statusText = getText(R.string.remove);break;
                    case Status.STATUS_IDLE:
                        statusIcon = R.drawable.icon_failed;
                        statusText = getText(R.string.failed);
                        if (null != result && result.isSucceed()) {
                            statusIcon = R.drawable.icon_succeed;
                            statusText = getText(R.string.succeed);
                        } else if (null != data && task instanceof DownloadTask) {
                            statusIcon = R.drawable.icon_download;
                        } else if (null != data && task instanceof UploadTask) {
                            statusIcon = R.drawable.icon_upload;
                        }
                        break;
                }
                taskBinding.setStatusIcon(statusIcon);
                taskBinding.setProgress(progress);
                taskBinding.setStatusText(statusText);
            }else if (binding instanceof ListEmptyBinding){
                ListEmptyBinding emptyBinding=(ListEmptyBinding)binding;
                String title=getText(R.string.empty);
                Drawable iconDrawable=getDrawable(R.drawable.icon_empty);
                emptyBinding.setIconDrawable(iconDrawable);
                emptyBinding.setTitle(title);
            }
        }
    }
}
