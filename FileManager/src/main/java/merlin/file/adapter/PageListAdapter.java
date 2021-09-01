package merlin.file.adapter;

import luckmerlin.core.Canceler;
import luckmerlin.core.Code;
import luckmerlin.core.OnFinish;
import luckmerlin.core.data.OnPageLoadFinish;
import luckmerlin.core.data.Page;
import luckmerlin.core.data.Pager;
import luckmerlin.core.debug.Debug;

public class PageListAdapter<A,T> extends ListAdapter<T>{
    private Pager<A,T> mPager;
    private Loading<T> mLoading=null;
    private Integer mLatestCode;
    private A mArg;

    public final Canceler setPager(Pager<A,T> pager){
        return setPager(pager,null);
    }

    public final Canceler setPager(Pager<A,T> pager,A arg){
        return setPager(pager,arg,-1);
    }

    public final Canceler setPager(Pager<A,T> pager,A arg,int limit){
        Pager<A,T> current=mPager;
        if ((null==pager&&null==current)||(null!=pager&&null!=current&&current==pager)){
            return null;//Not need
        }
        mPager=pager;
        Debug.D("Set page list adapter pager.");
        return reset(arg,limit);
    }

    public final Canceler pre(int limit){
        return pending(()-> loadPage(mArg, getFirst(),-Math.abs(limit),(int code, String note, Page<T> data)-> {
            if (code==Code.CODE_SUCCEED&&null!=data){
                insert(0,data.getData(),null);
            } }));
    }

    public final Canceler next(int limit){
        final int count=getDataCount();
        return pending(()->loadPage(mArg, getLatest(),Math.abs(limit),(int code, String note, Page<T> data)-> {
            if (code==Code.CODE_SUCCEED&&null!=data){
                insert(count,data.getData(),null);
            } }));
    }

    public final Canceler reset(A arg,int limit){
        return pending(()->loadPage(arg, null,limit,(int code, String note, Page<T> data)-> {
            if (code==Code.CODE_SUCCEED&&null!=data){
                super.set(data.getData(),null);
            } }));
    }

    protected void onPageLoadFinish(int code, String note, Page<T> data,A arg,T anchor,int limit){
        //Do nothing
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

    private final Canceler loadPage(A arg,T anchor,int limit,OnPageLoadFinish<T> callback){
        if (null!=mLoading){

            notifyFinish(Code.CODE_ALREADY_DOING,"Already doing.",null,callback);
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
                    notifyFinish(code,note,data,callback);
                    onPageLoadFinish(code,note,data,arg,anchor,limit);
                }else{
                    notifyFinish(code|Code.CODE_CANCEL,note,data,callback);
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
