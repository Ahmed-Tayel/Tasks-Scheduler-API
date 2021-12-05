package com.TaskScheduler.Services.Manager;

import java.util.List;

public class ClientRequestBody {
    public List<ClientRequestTask> tasks;

    public ClientRequestBody() {
    }

    public List<ClientRequestTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ClientRequestTask> tasks) {
        this.tasks = tasks;
    }

}
