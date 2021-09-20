package merlin.file.model;

import java.util.List;

import luckmerlin.databinding.model.Model;
import luckmerlin.task.OnTaskUpdate;
import luckmerlin.task.Tasked;
import merlin.file.adapter.ProcessTaskListAdapter;

public class TaskProcessModel extends Model implements OnTaskUpdate {
    private ProcessTaskListAdapter mListAdapter=new ProcessTaskListAdapter();

    public final boolean add(List<Tasked> taskeds){
        return null!=taskeds&&mListAdapter.add(taskeds,true);
    }

    @Override
    public void onTaskUpdate(int status, Tasked task, Object arg) {
        mListAdapter.notifyChildChanged(task);
    }

    public final ProcessTaskListAdapter getListAdapter() {
        return mListAdapter;
    }
}
