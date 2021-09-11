package luckmerlin.task;

public interface Progress {
    long getDone();
    long getTotal();
    long getSpeed();
    String getTitle();

    default float getProgress(){
        long total=getTotal();
        return total>0?getDone()*100.0f/total:0;
    }
}
