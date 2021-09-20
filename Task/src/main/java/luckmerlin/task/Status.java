package luckmerlin.task;

public interface Status {
    int STATUS_IDLE=0;
    int STATUS_WAIT=2;
    int STATUS_PREPARE=3;
    int STATUS_DOING=4;
    int STATUS_RECHECK=5;
    int STATUS_ADD=6;
    int STATUS_REMOVE=7;
}
