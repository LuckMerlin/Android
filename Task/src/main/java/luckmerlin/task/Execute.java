package luckmerlin.task;

import luckmerlin.core.Result;

public interface Execute extends Status {
    int getStatus();
    Result getResult();
    Progress getProgress();
}
