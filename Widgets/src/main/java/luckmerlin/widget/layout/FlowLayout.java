package luckmerlin.widget.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import luckmerlin.core.debug.Debug;

public class FlowLayout extends ViewGroup {
    private Map<View,int[]> mViewsMap;

    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count=getChildCount();
        View child=null;
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        int paddingLeft=getPaddingLeft();int paddingRight=getPaddingRight();
        int paddingTop=getPaddingTop();int paddingBottom=getPaddingBottom();
        int marginLeft=0;int marginRight=0;int marginTop=0;int marginBottom=0;
        int lineUsedWidth=0;int lineUsedHeight=0;
        int usedWidth=0;int usedHeight=0;
        final Map<View,int[]> viewsMap=mViewsMap=new WeakHashMap<>();
        for (int i = 0; i < count; i++) {
            if (null==(child=getChildAt(i))||child.getVisibility()==View.GONE){
                continue;
            }
            ViewGroup.LayoutParams params=child.getLayoutParams();
            params=null==params?new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT):params;
            if (params instanceof MarginLayoutParams){
                MarginLayoutParams marginParams=(MarginLayoutParams)params;
                marginLeft=marginParams.leftMargin;marginRight=marginParams.rightMargin;
                marginTop=marginParams.topMargin;marginBottom=marginParams.bottomMargin;
            }
            int childWidthMeasureSpec=getChildMeasureSpec(widthMeasureSpec,
                    paddingLeft+paddingRight+marginLeft+marginRight,params.width);
            int childHeightMeasureSpec=getChildMeasureSpec(heightMeasureSpec,
                    paddingTop+paddingBottom+marginTop+marginBottom,params.height);
            measureChild(child,childWidthMeasureSpec,childHeightMeasureSpec);
            int measureWidth=child.getMeasuredWidth();
            int measureHeight=child.getMeasuredHeight();
            lineUsedWidth+=measureWidth;lineUsedHeight=Math.max(measureHeight,lineUsedHeight);
            if (lineUsedWidth>width){
                usedWidth=Math.max(usedWidth,lineUsedWidth-measureWidth);
                usedHeight+=lineUsedHeight;
                lineUsedWidth=measureWidth;lineUsedHeight=measureHeight;
            }
            viewsMap.put(child,new int[]{lineUsedWidth,usedHeight+lineUsedHeight});
        }
        setMeasuredDimension(width=Math.max(lineUsedWidth,usedWidth),height=Math.max(lineUsedHeight,usedHeight));
        Debug.D("EEEEE的点点滴滴 EEEEEEE  "+width+" "+height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count=getChildCount();
        View child=null;
        Map<View,int[]> viewsMap=mViewsMap;
        mViewsMap=null;
        if (null!=viewsMap&&viewsMap.size()>0){
            for (int i = 0; i < count; i++) {
                if (null==(child=getChildAt(i))||child.getVisibility()==View.GONE){
                    continue;
                }
                int[] position=viewsMap.get(child);
                if (null==position||position.length!=2){
                    continue;
                }
                r=position[0];b=position[1];
                child.layout(r-child.getMeasuredWidth(),b-child.getMeasuredHeight(),r,b);
            }
        }
    }
}
