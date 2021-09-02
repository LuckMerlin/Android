package merlin.file.view;

import android.content.Context;
import android.util.AttributeSet;
import merlin.file.adapter.Refresher;

public class SwipeRefreshLayout extends androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        implements Refresher {

    public SwipeRefreshLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLayout(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnRefreshListener(Refresher.OnRefreshListener listener) {
        super.setOnRefreshListener(null!=listener?()-> listener.onRefresh(Refresher.TOP) :null);
    }

    @Override
    public boolean setRefreshing(boolean refreshing, int where) {
        if (where==Refresher.TOP){
            super.setRefreshing(refreshing);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRefreshing(int where) {
        return where==Refresher.TOP&&super.isRefreshing();
    }
}
