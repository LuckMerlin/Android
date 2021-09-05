package luckmerlin.task;

public interface Status {
    int STATUS_IDLE= 0;
    int STATUS_START=1;
    int STATUS_PREPARE=2;
    int STATUS_DOING=3;
    int STATUS_PAUSE=4;
    int STATUS_CANCEL=5;
    int STATUS_FINISH=6;
}
