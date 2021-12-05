package com.TaskScheduler.Services.Algorithms;

import org.springframework.stereotype.Component;

@Component
public class Task {
    public daysEnum day;
    public String name;
    public int start;
    public int end;
    public int interval;
    public int hours;
    public int priority;

    public Task(){

    }

    public Task(daysEnum day, String name, int start, int end, int interval, int hours, int priority){
        this.day = day;
        this.name = name;
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.hours = hours;
        this.priority = priority;
    }
    
    public Task Clone(){
        Task cloneTask = new Task();
        cloneTask.day = this.day;
        cloneTask.name = this.name;
        cloneTask.start = this.start;
        cloneTask.end = this.end;
        cloneTask.interval = this.interval;
        cloneTask.hours = this.hours;
        cloneTask.priority = this.priority;
        return cloneTask;
    }

    public void print(){
        System.out.println("Day: " + this.day + " ," + "name: " + this.name
                + " ," + "start: " + this.start
                + " ," + "end: " + this.end
                + " ," + "interval: " + this.interval
                + " ," + "hours: " + this.hours
                + " ," + "priority: " + this.priority);
    }

    public daysEnum getDay() {
        return day;
    }

    public void setDay(daysEnum day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
