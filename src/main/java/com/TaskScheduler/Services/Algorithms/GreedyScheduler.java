package com.TaskScheduler.Services.Algorithms;

import com.TaskScheduler.Services.Manager.Manager;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

@Service
public class GreedyScheduler implements Scheduler{

    public Tasks task_data_arr;
    public Iterator task_data_arr_iter;
    public Task     current_task;
    public Manager manager;
    public Schedule posterior_schedule;
    public final int PRIORITIES = 3;

    public GreedyScheduler() {}

    @Override
    public Schedule runAlgo(Tasks data, Manager uiManager){
        manager = uiManager;
        task_data_arr = data;
        Schedule prior_schedule = new Schedule();

        split_two_days_tasks(task_data_arr);

        task_data_arr_iter = task_data_arr.iterator();
        while(task_data_arr_iter.hasNext()){
            current_task = (Task) task_data_arr_iter.next();
            prior_schedule.add_task(current_task.day,current_task);
        }

        posterior_schedule = startScheduler(prior_schedule);
        return posterior_schedule;
    }

    private Schedule startScheduler(Schedule priorSchedule){
        Tasks[] prioritiesTasks = new Tasks[3];
        schedulePreProcessing(priorSchedule);
        Schedule outputSchedule = new Schedule();
        for (daysEnum day: daysEnum.values()){
            if (day == daysEnum.EVERY){continue;}
            for (int priority =1; priority <= PRIORITIES; priority ++){
                Tasks currentPrioTasks = get_priority_tasks(priorSchedule.get_day_tasks(day), priority);
                Tasks scheduledTasks = scheduleTasks(currentPrioTasks, priority, prioritiesTasks);
                prioritiesTasks[priority - 1] = scheduledTasks;
            }
            merge_tasks(prioritiesTasks[0], prioritiesTasks[1]);
            merge_tasks(prioritiesTasks[0], prioritiesTasks[2]);

            outputSchedule.add_day_tasks(day, allocate_tasks_earliest_finish_time(prioritiesTasks[0]));
        }
        return outputSchedule;
    }

    private void schedulePreProcessing(Schedule priorSchedule){
        allocate_intervals_to_schedule(priorSchedule);
        allocate_everyday_tasks(priorSchedule);
    }

    private void allocate_intervals_to_schedule(Schedule priorSchedule){
        for(daysEnum day: daysEnum.values()){
            Tasks currentTasks = priorSchedule.get_day_tasks(day);
            Iterator<Task> iter = currentTasks.iterator();
            while (iter.hasNext()){
                Task task = iter.next();
                allocate_intervals_to_task(task);
            }
        }
    }

    private void allocate_everyday_tasks(Schedule priorSchedule){
        Tasks tasks = priorSchedule.get_day_tasks(daysEnum.EVERY);
        Iterator<Task> tasksIter = tasks.iterator();
        while (tasksIter.hasNext()){
            Task currentTask = tasksIter.next();
            for (int indexDays =0; indexDays < daysEnum.values().length - 1; indexDays ++){
                Task currentTaskDay =  currentTask.Clone();
                currentTaskDay.day = daysEnum.values()[indexDays];
                priorSchedule.add_task(indexDays, currentTaskDay);
            }
        }
        priorSchedule.clear_day_tasks(daysEnum.EVERY);
    }

    private void split_two_days_tasks(Tasks tasks) {
        Tasks modifiedTasks = new Tasks();
        Iterator<Task> tasksIter = tasks.iterator();
        while (tasksIter.hasNext()){
            Task task = tasksIter.next();
            if (task.start > task.end){
                tasksIter.remove();
                modifiedTasks.add_task(new Task(task.day,task.name,task.start,24,24-task.start,0,task.priority));
                modifiedTasks.add_task(new Task(get_next_day(task.day),task.name,0,task.end,task.interval,0,task.priority));
            }
        }

        Iterator<Task> modifiedTasksIter = modifiedTasks.iterator();
        while(modifiedTasksIter.hasNext()){
            tasks.add_task(modifiedTasksIter.next());
        }
    }

    private daysEnum get_next_day(daysEnum day){
        daysEnum nextDay = null;
        switch (day){
            case SAT:
                nextDay = daysEnum.SUN;
                break;
            case SUN:
                nextDay = daysEnum.MON;
                break;
            case MON:
                nextDay = daysEnum.TUES;
                break;
            case TUES:
                nextDay = daysEnum.WED;
                break;
            case WED:
                nextDay = daysEnum.THURS;
                break;
            case THURS:
                nextDay = daysEnum.FRI;
                break;
            case FRI:
                nextDay = daysEnum.SAT;
                break;
            case EVERY:
                nextDay = daysEnum.EVERY;
                break;
            default: break;
        }
        return nextDay;
    }

    private Tasks allocate_tasks_earliest_finish_time(Tasks tasks){
        Tasks output_tasks = new Tasks();
        PriorityQueue<Task> min_heap_startTime = new PriorityQueue<Task>(new TaskComparatorStartTime());
        PriorityQueue<Task> min_heap_endTime = new PriorityQueue<Task>(new TaskComparatorEndTime());
        Iterator<Task> task_iterator = tasks.iterator();
        Task current_task;
        Task current_task_min_end_time;
        Task current_task_min_start_time;

        while (task_iterator.hasNext()) {
            current_task = task_iterator.next();
            min_heap_startTime.add(current_task);
            min_heap_endTime.add(current_task);
        }

        while(!min_heap_endTime.isEmpty()){
            current_task_min_end_time =  min_heap_endTime.poll();
            output_tasks.add_task(current_task_min_end_time);
            while(!min_heap_startTime.isEmpty()){
                current_task_min_start_time = min_heap_startTime.peek();
                if(current_task_min_start_time.start < current_task_min_end_time.end){
                    //Incompatible task, remove it
                    min_heap_endTime.remove(current_task_min_start_time);
                    min_heap_startTime.remove(current_task_min_start_time);
                }
                else{
                    break;
                }
            }
        }
        return output_tasks;
    }

    private void merge_hoursBased_tasks(Tasks FromToTasks, Tasks hourTasks){
        Tasks fromTofreeSlotsTasks = extract_fromToBased_freeSlots_tasks(FromToTasks);

        PriorityQueue<Task> min_heap_hoursBased_tasks = new PriorityQueue<Task>(new TaskComparatorInterval());
        PriorityQueue<Task> min_heap_freeSlots_tasks = new PriorityQueue<Task>(new TaskComparatorInterval());

        Iterator<Task> freeSlotsIter = fromTofreeSlotsTasks.iterator();
        Iterator<Task> hourBasedTasksIter = hourTasks.iterator();

        while(freeSlotsIter.hasNext()){
            min_heap_freeSlots_tasks.add(freeSlotsIter.next());
        }

        while(hourBasedTasksIter.hasNext()){
            min_heap_hoursBased_tasks.add(hourBasedTasksIter.next());
        }

        if (FromToTasks.isEmpty()){
            int startTime = 0;
            while(!min_heap_hoursBased_tasks.isEmpty()){
                Task hourTask = min_heap_hoursBased_tasks.poll();
                if (startTime + hourTask.interval > 24){break;}
                else {
                    hourTask.start = startTime;
                    hourTask.end = startTime + hourTask.interval;
                    hourTask.hours = 0;
                    FromToTasks.add_task(hourTask);
                    startTime += hourTask.interval;
                }
            }
        }

        else{
            while(!min_heap_hoursBased_tasks.isEmpty() && !min_heap_freeSlots_tasks.isEmpty()){
                Task freeSlotTask = min_heap_freeSlots_tasks.peek();
                Task candidateTask = min_heap_hoursBased_tasks.peek();

                if (candidateTask.interval <= freeSlotTask.interval){
                    min_heap_hoursBased_tasks.poll();
                    min_heap_freeSlots_tasks.poll();

                    candidateTask.start = freeSlotTask.start;
                    candidateTask.end = candidateTask.start + candidateTask.hours;
                    candidateTask.hours = 0;
                    FromToTasks.add_task(candidateTask);

                    Tasks splittedfreeSlotsTasks = get_splitted_freeSlot_after_merge(candidateTask, freeSlotTask);
                    Iterator<Task> splittedFreeSlotsTasksIter = splittedfreeSlotsTasks.iterator();
                    while(splittedFreeSlotsTasksIter.hasNext()){
                        min_heap_freeSlots_tasks.add(splittedFreeSlotsTasksIter.next());
                    }
                }

                else{
                    min_heap_freeSlots_tasks.poll();
                }
            }
        }
    }

    private void merge_tasks(Tasks mainTasks, Tasks lowPriorityTasks){
        if (mainTasks.isEmpty()){
            Iterator<Task> lowPriotityTasksIter = lowPriorityTasks.iterator();
            while(lowPriotityTasksIter.hasNext()){
                mainTasks.add_task(lowPriotityTasksIter.next());
            }
        }
        else {
            Tasks freeSlots = extract_fromToBased_freeSlots_tasks(mainTasks);
            PriorityQueue<Task> min_heap_freeSlots_tasks = new PriorityQueue<Task>(new TaskComparatorStartTime());
            Iterator<Task> freeSlotsIter = freeSlots.iterator();

            PriorityQueue<Task> min_heap_toBeMerged_tasks = new PriorityQueue<Task>(new TaskComparatorStartTime());
            Iterator<Task> lowPriotityTasksIter = lowPriorityTasks.iterator();


            while(freeSlotsIter.hasNext()){
                min_heap_freeSlots_tasks.add(freeSlotsIter.next());
            }

            while(lowPriotityTasksIter.hasNext()){
                min_heap_toBeMerged_tasks.add(lowPriotityTasksIter.next());
            }

            while(!min_heap_freeSlots_tasks.isEmpty() && !min_heap_toBeMerged_tasks.isEmpty()){
                Task candidateTask = min_heap_toBeMerged_tasks.peek();
                Task freeSlotTask = min_heap_freeSlots_tasks.peek();

                if (candidateTask.start < freeSlotTask.start){
                    min_heap_toBeMerged_tasks.poll();
                }

                else if (candidateTask.start > freeSlotTask.end){
                    min_heap_freeSlots_tasks.poll();
                }

                else if (candidateTask.end > freeSlotTask.end){
                    min_heap_toBeMerged_tasks.poll();
                }

                else{
                    min_heap_toBeMerged_tasks.poll();
                    min_heap_freeSlots_tasks.poll();

                    mainTasks.add_task(candidateTask);
                    Tasks splittedFreeSlotsTasks =  get_splitted_freeSlot_after_merge(candidateTask, freeSlotTask);

                    Iterator<Task> splittedFreeSlotsTasksIter = splittedFreeSlotsTasks.iterator();
                    while(splittedFreeSlotsTasksIter.hasNext()){
                        min_heap_freeSlots_tasks.add(splittedFreeSlotsTasksIter.next());
                    }
                }
            }
        }
    }

    private Tasks get_splitted_freeSlot_after_merge(Task mergedTask, Task freeSlotTask){
        Tasks tasks = new Tasks();
        if (mergedTask.start > freeSlotTask.start){
            Task splittedTask = new Task();
            splittedTask.start = freeSlotTask.start;
            splittedTask.end = mergedTask.start;
            allocate_intervals_to_task(splittedTask);
            tasks.add_task(splittedTask);
        }

        if (mergedTask.end < freeSlotTask.end){
            Task splittedTask = new Task();
            splittedTask.start = mergedTask.end;
            splittedTask.end = freeSlotTask.end;
            allocate_intervals_to_task(splittedTask);
            tasks.add_task(splittedTask);
        }

        return tasks;
    }

    //The input array should be sorted by Earliest Finish Time (EFT) Algorithm.
    private Tasks extract_fromToBased_freeSlots_tasks(Tasks tasks){
        Tasks output = new Tasks();
        int tempStartTime = 0;
        int tempEndTime = 0;

        boolean firstTaskFlag = true;

        Iterator<Task> iter = tasks.iterator();
        while(iter.hasNext()){
            Task currentTask = iter.next();

            if (firstTaskFlag == true){
                if (currentTask.start > 0){
                    Task freeslot = new Task();
                    freeslot.start = 0;
                    freeslot.end = currentTask.start;
                    allocate_intervals_to_task(freeslot);
                    output.add_task(freeslot);
                }
                tempStartTime = currentTask.end;
                firstTaskFlag = false;
            }

            else {
                tempEndTime = currentTask.start;
                Task freeslot = new Task();
                freeslot.start = tempStartTime;
                freeslot.end = tempEndTime;
                allocate_intervals_to_task(freeslot);
                output.add_task(freeslot);

                tempStartTime = currentTask.end;
            }

            if (!iter.hasNext()){
                if (currentTask.end < 24){
                    Task freeslot = new Task();
                    freeslot.start = currentTask.end;
                    freeslot.end = 24;
                    allocate_intervals_to_task(freeslot);
                    output.add_task(freeslot);
                }
            }
        }
        return output;
    }

    private Tasks get_priority_tasks(Tasks tasks,int priority){
        Tasks output = new Tasks();
        Iterator<Task> iter = tasks.iterator();
        while(iter.hasNext()){
            Task currentTask = iter.next();
            if (currentTask.priority == priority){
                output.add_task(currentTask);
            }
        }
        return output;
    }

    private Tasks extract_hoursBased_tasks(Tasks tasks){
        Tasks output = new Tasks();
        Iterator<Task> iter = tasks.iterator();
        while(iter.hasNext()){
            Task currentTask = iter.next();
            if (currentTask.hours != 0){
                output.add_task(currentTask);
            }
        }
        return output;
    }

    private Tasks extract_FromTo_tasks(Tasks tasks){
        Tasks output = new Tasks();
        Iterator<Task> iter = tasks.iterator();
        while(iter.hasNext()){
            Task currentTask = iter.next();
            if (currentTask.hours == 0){
                output.add_task(currentTask);
            }
        }
        return output;
    }



    private Tasks scheduleTasks(Tasks tasks, int priority, Tasks[] prioritiesTasks){
        Tasks outputTasks = new Tasks();
        Tasks fromToBasedTasksSorted = new Tasks();

        //extract hours based tasks
        Tasks hourBasedTasks = extract_hoursBased_tasks(tasks);

        //extract from_to_based tasks, sort it, and extract freeslots tasks
        Tasks fromToBasedTasks = extract_FromTo_tasks(tasks);

        if (fromToBasedTasks.isEmpty() && priority > 1 ){
            merge_hoursBased_tasks(prioritiesTasks[priority - 2], hourBasedTasks);
            prioritiesTasks[priority - 2] = allocate_tasks_earliest_finish_time(prioritiesTasks[priority - 2]);
        }

        else {
            fromToBasedTasksSorted = allocate_tasks_earliest_finish_time(fromToBasedTasks);
            //Merge hoursbased tasks with sorted fromTo tasks
            merge_hoursBased_tasks(fromToBasedTasksSorted, hourBasedTasks);
            //Sort the output tasks
            outputTasks = allocate_tasks_earliest_finish_time(fromToBasedTasksSorted);
        }

        return outputTasks;
    }


    private void allocate_intervals_to_task(Task task){
        if (task.hours != 0){
            task.interval = task.hours;
        }
        else if (task.end >= task.start){
            task.interval = task.end - task.start;
        }
        else{
            task.interval = 24 - task.start + task.end;
        }
    }



    private class TaskComparatorEndTime implements Comparator<Task> {

        @Override
        public int compare(Task o1, Task o2) {
            int retVal = 0;

            if (o1.end < o2.end){
                retVal = -1;
            }
            else if (o1.end > o2.end){
                retVal = 1;
            }

            return retVal;
        }
    }

    private class TaskComparatorStartTime implements Comparator<Task>{
        @Override
        public int compare(Task o1, Task o2) {
            int retVal = 0;

            if (o1.start < o2.start){
                retVal = -1;
            }
            else if (o1.start > o2.start){
                retVal = 1;
            }

            return retVal;
        }
    }

    private class TaskComparatorInterval implements Comparator<Task>{
        @Override
        public int compare(Task o1, Task o2) {
            int retVal = 0;

            if (o1.interval < o2.interval){
                retVal = -1;
            }
            else if (o1.interval > o2.interval){
                retVal = 1;
            }

            return retVal;
        }
    }


}
