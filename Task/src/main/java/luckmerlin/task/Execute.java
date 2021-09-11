package luckmerlin.task;

public interface Execute extends Status {
    int getStatus();
    Result getResult();
    Progress getProgress();
}
