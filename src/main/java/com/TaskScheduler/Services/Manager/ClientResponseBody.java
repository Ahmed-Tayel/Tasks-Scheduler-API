package com.TaskScheduler.Services.Manager;

import com.TaskScheduler.Services.Algorithms.Schedule;
import com.TaskScheduler.Services.Algorithms.Task;
import com.TaskScheduler.Services.Algorithms.Tasks;
import com.TaskScheduler.Services.Algorithms.daysEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ClientResponseBody {
    public List<ClientResponseTask> saturday;
    public List<ClientResponseTask> sunday;
    public List<ClientResponseTask> monday;
    public List<ClientResponseTask> tuesday;
    public List<ClientResponseTask> wednesday;
    public List<ClientResponseTask> thursday;
    public List<ClientResponseTask> friday;

    public ClientResponseBody(Schedule algoOutput) {
        saturday = convertTask(algoOutput.get_day_tasks(daysEnum.SAT));
        sunday = convertTask(algoOutput.get_day_tasks(daysEnum.SUN));
        monday = convertTask(algoOutput.get_day_tasks(daysEnum.MON));
        tuesday = convertTask(algoOutput.get_day_tasks(daysEnum.TUES));
        wednesday = convertTask(algoOutput.get_day_tasks(daysEnum.WED));
        thursday = convertTask(algoOutput.get_day_tasks(daysEnum.THURS));
        friday = convertTask(algoOutput.get_day_tasks(daysEnum.FRI));
    }

    public List<ClientResponseTask> convertTask(Tasks tasks){
        List<ClientResponseTask> outputTasks = new ArrayList<ClientResponseTask>();
        Iterator tasksIter = tasks.iterator();
        while (tasksIter.hasNext()){
            ClientResponseTask outputTask = new ClientResponseTask();
            Task task = (Task) tasksIter.next();
            outputTask.setName(task.getName());
            outputTask.setStart(task.getStart());
            outputTask.setEnd(task.getEnd());
            outputTasks.add(outputTask);
        }
        return outputTasks;
    }
}
