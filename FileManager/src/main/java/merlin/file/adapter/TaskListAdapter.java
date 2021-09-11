package merlin.file.adapter;

import android.view.ViewGroup;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.file.manager.R;
import com.file.manager.databinding.ItemTaskBinding;
import java.util.List;
import luckmerlin.task.Task;

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
        }
    }
}
