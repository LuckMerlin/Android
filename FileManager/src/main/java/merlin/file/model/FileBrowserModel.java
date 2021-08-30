package merlin.file.model;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;
import com.merlin.file.Client;
import com.merlin.file.Folder;
import com.merlin.file.Mode;
import luckmerlin.databinding.Model;

public class FileBrowserModel extends Model {
    private final ObservableField<Client> mCurrentClient=new ObservableField<>();
    private final ObservableField<Mode> mCurrentMode=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mClientCount=new ObservableField<>();
    private final ObservableField<RecyclerView.Adapter> mContentAdapter=new ObservableField<>();

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
