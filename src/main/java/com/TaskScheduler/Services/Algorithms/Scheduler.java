package com.TaskScheduler.Services.Algorithms;

import com.TaskScheduler.Services.Manager.Manager;

public interface Scheduler {
    public Schedule runAlgo(Tasks data, Manager uiManager);
}
