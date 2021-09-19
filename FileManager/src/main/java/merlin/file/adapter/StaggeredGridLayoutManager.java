package merlin.file.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

public class StaggeredGridLayoutManager extends androidx.recyclerview.widget.StaggeredGridLayoutManager {

    public StaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        //Make empty type view layout center
        if (getChildCount()==1){
            View view=getChildAt(0);
            if (null!=view&& ListAdapter.TYPE_EMPTY==getItemViewType(view)) {
                ViewParent parent=view.getParent();
                if (null!=parent&&parent instanceof View){
                    int width=view.getWidth();
                    int height=view.getHeight();
                    View parentView=(View)parent;
                    int left=(parentView.getWidth()-width)>>1;int top=(parentView.getHeight()-height)>>1;
                    view.layout(left, top ,left+width, top+height);
                }
            }
        }
    }
}
