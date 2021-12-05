package com.TaskScheduler.Services.Manager;


import com.TaskScheduler.Exceptions.BadRequestException;
import com.TaskScheduler.Services.Algorithms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class Manager {
    public Tasks schedulerTasks;
    @Autowired
    public Scheduler scheduler;
    @Autowired
    public ClientInputValidation inputValidator;

    public Manager(Scheduler current_scheduler, ClientInputValidation validator) throws Exception{
        this.scheduler = current_scheduler;
        this.inputValidator = validator;
    }

    public ClientResponseBody update_tasks(ClientRequestBody reqBody) throws BadRequestException {
        inputValidator.validateInput(reqBody);
        schedulerTasks = prepareClientInput(reqBody);
        return new ClientResponseBody(scheduler.runAlgo(schedulerTasks,this));
    }

    private Tasks prepareClientInput(ClientRequestBody reqBody){
        Tasks schedulerTasks = new Tasks();
        Iterator clientReqTasksIter = reqBody.getTasks().iterator();
        while (clientReqTasksIter.hasNext()){
            ClientRequestTask currentClientRequestTask = (ClientRequestTask) clientReqTasksIter.next();
            schedulerTasks.add_task(createSchedulerTask(currentClientRequestTask));
        }
        return schedulerTasks;
    }

    private Task createSchedulerTask(ClientRequestTask currentTask){
        Task returnTask = new Task();

        returnTask.setName(currentTask.getName());
        returnTask.setStart(currentTask.getStart());
        returnTask.setEnd(currentTask.getEnd());
        returnTask.setInterval(currentTask.getInterval());
        returnTask.setHours(currentTask.getHours());
        returnTask.setPriority(currentTask.getPriority());

        switch (currentTask.getDay()){
            case "Everyday":
                returnTask.setDay(daysEnum.EVERY);
                break;

            case "Saturday":
                returnTask.setDay(daysEnum.SAT);
                break;

            case "Sunday":
                returnTask.setDay(daysEnum.SUN);
                break;

            case "Monday":
                returnTask.setDay(daysEnum.MON);
                break;

            case "Tuesday":
                returnTask.setDay(daysEnum.TUES);
                break;

            case "Wednesday":
                returnTask.setDay(daysEnum.WED);
                break;

            case "Thursday":
                returnTask.setDay(daysEnum.THURS);
                break;

            case "Friday":
                returnTask.setDay(daysEnum.FRI);
                break;

            default:
                return null;
        }
        return returnTask;
    }
}
