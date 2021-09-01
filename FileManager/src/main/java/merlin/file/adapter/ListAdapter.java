package merlin.file.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import luckmerlin.core.debug.Debug;

public abstract class ListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<T> mData;
    private Handler mHandler;
    public final static int TYPE_NONE=0;
    public final static int TYPE_TAIL=-1;
    public final static int TYPE_EMPTY=-3;
    public final static int TYPE_DATA=-4;
    public final static int TYPE_HEAD=-5;
    private WeakReference<RecyclerView> mRecyclerView;
    private final SparseArray<RecyclerView.ViewHolder> mFixHolder=new SparseArray<>();

    public ListAdapter(){
        this(null);
    }

    public ListAdapter(ArrayList<T>  list){
        if (null!=list&&list.size()>0) {
            add(list,true,null);
        }
    }

    public final T getFirst(){
        ArrayList<T> data=mData;
        return null!=data&&data.size()>0?data.get(0):null;
    }

    public final T getLatest(){
        ArrayList<T> data=mData;
        int size=null!=data?data.size():-1;
        return size>=0?data.get(size-1):null;
    }

    protected Integer onResolveViewTypeLayoutId(int viewType){
        return null;
    }

    protected RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater in,int viewType,ViewGroup parent){
        return null;
    }

    public final boolean setFixHolder(int type,View view){
        return null!=view&&null==view.getParent()&&setFixHolder(type,new RecyclerView.ViewHolder(view){});
    }

    public final boolean setFixHolder(int type,RecyclerView.ViewHolder viewHolder){
        SparseArray<RecyclerView.ViewHolder> array=mFixHolder;
        if (null!=array){
            RecyclerView.ViewHolder curr=array.get(type);
            if ((null==curr&&null==viewHolder)||(null!=curr&&null!=viewHolder&&curr==viewHolder)){
                return false;
            }
            array.put(type,viewHolder);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @Override
  public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final LayoutInflater in=LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder=onCreateViewHolder(in,viewType,parent);
        if (viewHolder ==null){
           Integer integer= onResolveViewTypeLayoutId(viewType);
           ViewDataBinding binding=null!=integer&&integer!= Resources.ID_NULL?DataBindingUtil.inflate(in,integer,parent,false):null;
           viewHolder= null!=binding?new ViewHolder(binding):null;
        }
        if (null==viewHolder){
            switch (viewType){
                case TYPE_HEAD:
                    SparseArray<RecyclerView.ViewHolder> fixHolder=mFixHolder;
                    viewHolder=null!=fixHolder?fixHolder.get(TYPE_HEAD):null;
                    break;
                case TYPE_TAIL:
                    fixHolder=mFixHolder;
                    viewHolder=null!=fixHolder?fixHolder.get(TYPE_TAIL):null;
                    break;
                case TYPE_EMPTY:
                    fixHolder=mFixHolder;
                    viewHolder=null!=fixHolder?fixHolder.get(TYPE_EMPTY):null;
                    break;
                default:
                    viewHolder= new BaseViewHolder(new View(parent.getContext()));
                    break;
            }
        }
       return viewHolder;
    }

  protected void onBindViewHolder(RecyclerView.ViewHolder holder,int viewType,ViewDataBinding binding,
                                  int position,T data, List<Object> payloads){
        //Do nothing
  }

   @Override
  public final void onBindViewHolder( RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
      super.onBindViewHolder(holder, position, payloads);
      ViewDataBinding binding=null!=holder&&holder instanceof ViewHolder?((ViewHolder)holder).getBinding():null;
      T data=getItemData(position);
      onBindViewHolder(holder,null!=holder?holder.getItemViewType():TYPE_NONE,binding,position,data,payloads);
      if (null==payloads||payloads.isEmpty()){
           onBindViewHolder(holder, position);
       } else {
//           View itemView=holder.itemView;
//           if (null!=itemView){
//               itemView.setVisibility(((Item)payloads.get(0)).disabled ? View.VISIBLE : View.INVISIBLE);
//           }
       }
   }

    @Override
  public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Do nothing
    }

  public final T getItemData(int position){
        List<T> data=mData;
        return position>=0&&null!=data&&position<data.size()?data.get(position):null;
    }

    @Override
  public final int getItemCount() {
        int dataCount=getDataCount();
        SparseArray<RecyclerView.ViewHolder> fixHolder=mFixHolder;
        int increase=0;
        if (dataCount>0&&(null!=fixHolder&&null!=fixHolder.get(TYPE_HEAD))){
            increase++;
        }
        return (dataCount<=0?0:dataCount)+1+increase;
    }

  protected int getItemViewType(int position,int size) {
        return TYPE_DATA;
    }

   @Override
  public final int getItemViewType(int position) {
       List<T> data=mData;
       int size=null!=data?data.size():0;
       if (size<=0){
           return TYPE_EMPTY;
       }
       SparseArray<RecyclerView.ViewHolder> fixHolder=mFixHolder;
       if (position == 1&&(null!=fixHolder&&null!=fixHolder.get(TYPE_HEAD))){
           return TYPE_HEAD;
       }
       if (position == size){
           return TYPE_TAIL;
       }
       return getItemViewType(position,size);
    }

  public final int getDataCount(){
        List<T> data=mData;
        return null!=data?data.size():0;
    }

  public final boolean clean(){
        List<T> data=mData;
        int size=null!=data?data.size():0;
        if (size>0){
            data.clear();
            notifyItemRangeRemoved(0,size);
            return true;
        }
        return false;
  }

  public final ArrayList<T> getData() {
      ArrayList<T> data=mData;
      int length=null!=data?data.size():0;
      ArrayList<T> result=length>0?new ArrayList<>(length):null;
      return null!=result&&result.addAll(data)?result:null;
    }

  protected final static class BaseViewHolder extends RecyclerView.ViewHolder{
        protected BaseViewHolder(View root){
            super(root);
        }
   }

  public final static class ViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder{
    private V mBinding;

        protected ViewHolder(V binding){
        super(null!=binding?binding.getRoot():null);
        mBinding=binding;
    }

    public V getBinding() {
        return mBinding;
    }
}

    public final ArrayList<T> remove(T data,String debug){
        if (null!=data){
            List list=new ArrayList<>(1);
            list.add(data);
            return remove(list,debug);
        }
        return null;
    }

  public final ArrayList<T> remove(List<?> list,String debug){
        if (null!=list&&list.size()>0){
            ArrayList<T> removed=new ArrayList<>(list.size());
            T data=null;
            for (Object obj:list) {
                if (null!=(data=null!=obj?removeData(obj,debug):null)){
                    removed.add(data);
                }
            }
            return null!=removed&&removed.size()>0?removed:null;
        }
        return null;
    }

  public final T removeData(Object data,String debug){
        List<T> list=null!=data?mData:null;
        if (null!=list){
            synchronized (list){
                int index= null!=list?list.indexOf(data):-1;
                T indexData=index>=0?list.get(index):null;
                if (null!=indexData&&list.remove(indexData)){
                    notifyItemRemoved(index);
                    if (list.size()==0){
                        mData=null;
                    }
                    return indexData;
                }
            }
        }
        return null;
    }

  public final int index(Object data){
        List<T> list=null!=data?mData:null;
        return null!=list?list.indexOf(data):-1;
   }

  public final T get(Object data){
        List<T> list=null!=data?mData:null;
        if (null!=list){
            synchronized (list){
                int index= null!=list?list.indexOf(data):-1;
                return index>=0?list.get(index):null;
            }
        }
        return null;
    }

  public boolean set(Collection<T> data,String debug){
        List<T> list=mData;
        int size=null!=data?data.size():0;
        if (size>0){
            list=null!=list?list:(mData=new ArrayList<>(size));
            int currentSize=list.size();
            if (currentSize > size){
                list.removeAll(list.subList(size,currentSize));
                notifyItemRangeRemoved(size, currentSize);
            }
            list.clear();
            list.addAll(data);
            notifyItemRangeChanged(0,size,"Set");
        }else if(null!=list){
            list.clear();
            mData=null;
            notifyDataSetChanged();
            return true;
        }
      return false;
  }

  public final boolean add(T data,boolean exceptExist,String debug){
     List<T> list=null!=data?new ArrayList<>():null;
    return null!=list&&list.add(data)&&add(-1,list,exceptExist,debug);
   }

   public final boolean add(Collection<T> data,boolean exceptExist,String debug) {
    return add(-1,data,exceptExist,debug);
  }

  public final boolean add(int index,T data,boolean exceptExist,String debug) {
        if (null!=data){
            List<T> list=new ArrayList<T>(1);
            list.add(data);
            return add(index,list,exceptExist,debug);
        }
        return false;
  }

  public final boolean add(int index,Collection<T> data,boolean exceptExist,String debug) {
        if (null!=data&&data.size()>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            synchronized (list){
                index=index<0||index>list.size()?list.size():index;
                for (T child:data) {
                    if (null==child||(list.contains(child)&&exceptExist)){
                        continue;
                    }
                    list.add(index,child);
                    notifyItemInserted(index++);
                }
            }
            return true;
        }
        return false;
    }

  public final boolean replace(T data,String debug){
       List<T> list= null!=data?mData:null;
       int index=null!=list?list.indexOf(data):-1;
       return index>=0&&replace(index,data,debug);
   }

    public final boolean insert(int index,T data,String debug) {
        if (null!=data){
            List list=new ArrayList(1);
            list.add(data);
            return insert(index,list,debug);
        }
        return false;
    }

    public final boolean insert(int index, Collection<T> data, String debug) {
        return add(index, data, true,debug);
    }


  public final boolean replace(int index,T data,String debug) {
        if (null!=data&&index>=0){
            ArrayList<T> list= new ArrayList<>(1);
            list.add(data);
            return replace(index,list,debug);
        }
        return false;
    }

  public final boolean replace(int from,ArrayList<T> data,String debug){
        int length=null!=data?data.size():-1;
        if (length>0){
            ArrayList<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            if (null!=list){
                int size=list.size();
                from=from<0||from>size?size:from;
                synchronized (list){
                    for (int i = 0; i < length; i++) {
                        int index=i+from;
                        T child=data.get(i);
                        if (index<list.size()){
                            list.remove(list.get(index));
                            list.add(index,child);
                            notifyItemChanged(index,"Item replaced");
                        }else{
                            list.add(child);
                            notifyItemInserted(index);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

  public final boolean remove(int from,int to,String debug){
        ArrayList<T> list=from>=0&&to>from?mData:null;
        if (null!=list){
            synchronized (list){
                int size=list.size();
                if (size>0&&from<size){
                    to=to>size?size:to;
                    list.removeAll(list.subList(from,to));
                    notifyItemRangeRemoved(from,to-from);
                    Debug.D("Remove items from "+from+" to "+to+" "+(null!=debug?debug:"."));
                    return true;
                }
            }
        }
        return false;
    }

  public final boolean isExist(Object data){
        return null!=data&&index(data)>=0;
    }

  public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder,View view,ViewDataBinding binding){
        //Do nothing
    }

  @Override
  public final void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (null!=holder){
            View view=holder.itemView;
            ViewDataBinding binding=null!=view? DataBindingUtil.getBinding(view):null;
            onViewDetachedFromWindow(holder,view,binding);
        }
    }

  public void onAttachedRecyclerView(RecyclerView recyclerView) {
        //Do nothing
  }

  public final boolean updateItems(int from,int to){
        if (to>from&&from>=0&&from<getDataCount()){
            notifyItemRangeChanged(from,to-from);
            return true;
        }
        return false;
  }

  public final  RecyclerView.LayoutManager getLayoutManager(){
      RecyclerView rv=getRecyclerView();
      return null!=rv?rv.getLayoutManager():null;
  }

  public void onDetachedRecyclerView(RecyclerView recyclerView) {
     //Do nothing
  }

  public final Context getAdapterContext(){
        RecyclerView rv=getRecyclerView();
        return null!=rv?rv.getContext():null;
  }

   protected final boolean toast(Object text){
        Context context=null!=text?getAdapterContext():null;
        text=null!=context?text instanceof Integer?getText((Integer)text):text:null;
        if (null!=text&&text instanceof CharSequence){
            Toast.makeText(context,(CharSequence)text,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
   }

  protected final String getText(int textResId, Object ...args){
        Context context=getAdapterContext();
        return null!=context?context.getResources().getString(textResId,args):null;
   }

  public final RecyclerView getRecyclerView(){
        WeakReference<RecyclerView> reference = mRecyclerView;
        return null!=reference?reference.get():null;
  }

    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return null==rv?null:new LinearLayoutManager(rv.getContext(),RecyclerView.VERTICAL,false);
    }

    @Override
  public final void onAttachedToRecyclerView(RecyclerView recyclerView) {
     super.onAttachedToRecyclerView(recyclerView);
     WeakReference<RecyclerView> view=mRecyclerView;
     if (null!=view){
         mRecyclerView=null;
         view.clear();
     }
     if (null!=recyclerView){
         mRecyclerView=new WeakReference<>(recyclerView);
     }
      onAttachedRecyclerView(recyclerView);
  }

    @Override
    public final void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        WeakReference<RecyclerView> view=mRecyclerView;
        if (null!=view){
            mRecyclerView=null;
            view.clear();
        }
        onDetachedRecyclerView(recyclerView);
    }
}
