package com.TaskScheduler.Services.Algorithms;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class Tasks implements Iterable{

    private List<Task> tasks;

    public Tasks(){
        tasks = new ArrayList<Task>();
    }

    public void add_task(Task task){
        tasks.add(task);
    }

    public void remove(int index){
        tasks.remove(index);
    }

    public void clear(){
        tasks.clear();
    }

    public void print(){
        Iterator<Task> iter = tasks.iterator();
        while(iter.hasNext()){
            Task task = iter.next();
            task.print();
        }
    }

    public boolean isEmpty(){
        return tasks.isEmpty();
    }

    @Override
    public Iterator iterator() {
        return tasks.iterator();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
