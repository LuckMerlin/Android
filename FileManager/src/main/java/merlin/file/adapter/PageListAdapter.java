package merlin.file.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.ViewParent;
import androidx.recyclerview.widget.RecyclerView;
import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.OnFinish;
import luckmerlin.core.data.OnPageLoadFinish;
import luckmerlin.core.data.Page;
import luckmerlin.core.data.Pager;
import luckmerlin.core.debug.Debug;

public class PageListAdapter<A,T> extends ListAdapter<T> implements Refresher.OnRefreshListener{
    private Pager<A,T> mPager;
    private Loading<T> mLoading=null;
    private Integer mLatestCode;
    private A mArg;
    private A mLastSucceedArg;
    private int mPageSize=20;
    private Handler mHandler;

    public final PageListAdapter setPageSize(int pageSize){
        if (pageSize>0&&mPageSize!=pageSize){
            mPageSize=pageSize;
        }
        return this;
    }

    protected final boolean setRefreshing(boolean refreshing,int where){
        Refresher refresher=getRefresher();
        return null!=refresher&&refresher.setRefreshing(refreshing,where);
    }

    protected Refresher getRefresher(){
        RecyclerView recyclerView=getRecyclerView();
        ViewParent parent=null!=recyclerView?recyclerView.getParent():null;
        return null!=parent&&parent instanceof Refresher?(Refresher)parent:null;
    }

    public final boolean runOnUiThread(Runnable runnable,int delay){
        if (null==runnable){
            return false;
        }
        delay=delay<=0?0:delay;
        RecyclerView recyclerView=getRecyclerView();
        if (null!=recyclerView){
            return recyclerView.postDelayed(runnable,delay);
        }
        Handler handler=mHandler;
        return (null!=handler?handler:(mHandler=new Handler(Looper.getMainLooper()))).
                postDelayed(runnable,delay);
    }

    public final Pager<A, T> getPager() {
        return mPager;
    }

    public final A getArg() {
        return mArg;
    }

    @Override
    public void onRefresh(int where) {
        if (where==Refresher.TOP){
            pre(mPageSize, (int code, String note, Page<T> data)-> {
                if ((code&Code.CODE_CANCEL)==0){
                    PageListAdapter.this.setRefreshing(false,where);
                }
            });
        }else if (where==Refresher.BOTTOM){
            next(mPageSize,(int code, String note, Page<T> data)-> {
                if ((code&Code.CODE_CANCEL)==0){
                    PageListAdapter.this.setRefreshing(false,where);
                }
            });
        }
    }

    @Override
    public void onAttachedRecyclerView(RecyclerView recyclerView) {
        super.onAttachedRecyclerView(recyclerView);
        Refresher refresher=getRefresher();//Try bind refresher
        if (null!=refresher){
            refresher.setOnRefreshListener(this);
        }
        mHandler=null;
    }

    @Override
    public void onDetachedRecyclerView(RecyclerView recyclerView) {
        super.onDetachedRecyclerView(recyclerView);
        mLastSucceedArg=null;
        Refresher refresher=getRefresher();//Try unbind refresher
        if (null!=refresher){
            refresher.setOnRefreshListener(null);
        }
    }

    public final int getPageSize() {
        return mPageSize;
    }

    public final Canceler setPager(Pager<A,T> pager){
        return setPager(pager,null);
    }

    public final Canceler setPager(Pager<A,T> pager,A arg){
        return setPager(pager,arg,mPageSize);
    }

    public final Canceler setPager(Pager<A,T> pager,A arg,int limit){
        Pager<A,T> current=mPager;
        if ((null==pager&&null==current)||(null!=pager&&null!=current&&current.equals(pager))){
            return null;//Not need
        }
        mPager=pager;
        Debug.TD("Set page list adapter pager.",pager);
        return reset(arg,limit);
    }

    public final Canceler pre(){
        return pre(mPageSize,null);
    }

    public final Canceler pre(int limit,OnPageLoadFinish<T> callback){
        return pending(()-> loadPage(mArg, getFirst(),-Math.abs(limit),(int code, String note, Page<T> data)-> {
            if (code==Code.CODE_SUCCEED&&null!=data){
                insert(0,data.getData());
            }
            notifyFinish(code,note,data,callback);
        }));
    }

    public final Canceler next(){
        return next(mPageSize,null);
    }

    public final Canceler next(int limit,OnPageLoadFinish<T> callback){
        final int count=getDataCount();
        return pending(()->loadPage(mArg, getLatest(),Math.abs(limit),(int code, String note, Page<T> data)-> {
            if (code==Code.CODE_SUCCEED&&null!=data){
                insert(count,data.getData());
            }
            notifyFinish(code,note,data,callback);
        }));
    }

    public final Canceler reset(A arg){
        return reset(arg,mPageSize);
    }

    public final Canceler reset(A arg,int limit){
        mLastSucceedArg=null;
        return pending(()->loadPage(onPreResetLoad(arg), null,limit,(int code, String note, Page<T> data)-> {
            if ((code&Code.CODE_CANCEL)<=0){
                super.set(null!=data?data.getData():null);
            } }));
    }

    protected void onPageLoadFinish(int code, String note, Page<T> data,A arg,T anchor,int limit){
        //Do nothing
    }

    public final A getLastSucceedArg() {
        return mLastSucceedArg;
    }

    public final boolean isLatestCode(int ...codes){
        Integer code=mLatestCode;
        if (null!=code&&null!=codes){
            for (int child:codes) {
                if (child==code){
                    return true;
                }
            }
        }
        return false;
    }

    protected A onPreResetLoad(A arg){
        return arg;
    }

    private final Canceler loadPage(A arg,T anchor,int limit,OnPageLoadFinish<T> callback){
        if (null!=mLoading){
            notifyFinish(Code.CODE_ALREADY|Code.CODE_FAIL,"Already doing.",null,callback);
            return null;
        }
        Loading<T> loading=mLoading=new Loading<T>() {
            @Override
            public void onFinish(int code, String note, Page<T> data) {
                OnPageLoadFinish<T> current=mLoading;
                if (null!=current&&current==this){
                    mArg=arg;
                    mLoading=null;
                    mLatestCode=code;
                    mLastSucceedArg=code==Code.CODE_SUCCEED?arg:mLastSucceedArg;
                    runOnUiThread(()->{
                        notifyFinish(code,note,data,callback);
                        onPageLoadFinish(code,note,data,arg,anchor,limit);
                        },0);
                }else{
                    runOnUiThread(()->notifyFinish(code|Code.CODE_CANCEL,note,data,callback),0);
                }
                super.onFinish(code,note,data);
            }
        };
        Pager<A,T> pager=mPager;
        if (null==pager){
            notifyFinish(Code.CODE_SUCCEED,"None pager",null,loading);
            return null;
        }
        Canceler canceler=pager.onLoad(arg, anchor,limit,loading);
        loading.mCanceler=canceler;
        return canceler;
    }

    protected final<G> boolean notifyFinish(int code, String note, G data, OnFinish<G> callback){
        if (null!=callback){
            callback.onFinish(code,note,data);
            return true;
        }
        return false;
    }

    private Canceler pending(OnPendingFinish callback){
        if (null!=callback){
            Loading loading=mLoading;
            if (null==loading){
                return callback.onPendingFinish();
            }
            loading.cancel(callback);
            return (cancel)->false;
        }
        return null;
    }

    private static class Loading<T> implements OnPageLoadFinish<T>{
        private Canceler mCanceler;
        private OnPendingFinish mPending;

        boolean cancel(OnPendingFinish callback){
            mPending=callback;
            Canceler cancer=mCanceler;
            if (null!=cancer){
                cancer.cancel(true);
                return true;
            }
            return false;
        }

        @Override
        public void onFinish(int code, String note, Page<T> data) {
            OnPendingFinish pending=mPending;
            if (null!=pending){
                mPending=null;
                pending.onPendingFinish();
            }
        }
    }

    private interface OnPendingFinish{
        Canceler onPendingFinish();
    }

}
