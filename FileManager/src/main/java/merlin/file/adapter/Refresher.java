package merlin.file.adapter;

public interface Refresher {
    int TOP=0x01;
    int BOTTOM=0x02;
    interface OnRefreshListener{
        void onRefresh(int where);
    }

    void setOnRefreshListener(OnRefreshListener listener);
    boolean isRefreshing(int where);
    boolean setRefreshing(boolean refreshing,int where);
}
