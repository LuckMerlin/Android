package merlin.file.model;

import android.view.View;
import java.util.List;
import luckmerlin.databinding.model.Model;
import luckmerlin.databinding.touch.OnViewClick;
import luckmerlin.task.OnTaskUpdate;
import luckmerlin.task.Tasked;
import merlin.file.adapter.ProcessTaskListAdapter;

public class TaskProcessModel extends Model implements OnTaskUpdate, OnViewClick {
    private ProcessTaskListAdapter mListAdapter=new ProcessTaskListAdapter();

    public final boolean add(List<Tasked> taskeds){
        return null!=taskeds&&mListAdapter.add(taskeds,true);
    }

    @Override
    public boolean onClicked(View view, int id, int count, Object tag) {
        return false;
    }

    @Override
    public void onTaskUpdate(int status, Tasked task, Object arg) {
        runOnUiThread(()->mListAdapter.notifyChildChanged(task));
    }

    public final ProcessTaskListAdapter getListAdapter() {
        return mListAdapter;
    }
}
