package com.TaskScheduler.Controller;

import com.TaskScheduler.Services.Manager.ClientRequestBody;
import com.TaskScheduler.Services.Manager.ClientResponseBody;
import com.TaskScheduler.Services.Manager.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
class SchedulerController {
    @Autowired
    public Manager manager;

    SchedulerController(Manager manager) {
        this.manager = manager;
    }

    @PostMapping("/run")
    @ResponseBody
    ClientResponseBody startAlgo(@RequestBody ClientRequestBody reqBody) {
        ClientResponseBody output = manager.update_tasks(reqBody);
        return output;
    }
}