# To-do List Scheduler API in Spring Boot
This API is designed to schedule weekly to-do lists in an optimized way, there is no maximum limit for task items. The program will optimize the schedule according to the task priority, and the maximum number of tasks that could be fitted in the schedule for the same priority.
Also, the user has the privilege to add the item with a Start/End” format if the time is specific through the day, or in a “Hours” format if it just should be done through the day.

## Parameters
day: day's name which should be one of these values: "Everyday", "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" (Required Field)
name: Task's name which could be a number or a string (Required Field)
start: start time of the task in 24H format (Required Field with "end" if Hours is not specified)
end: end time of the task in 24H format (Required Field with "start" if Hours is not specified)
priority: could be in range of 1 to 3 (1 is the highest) (Required Field)
Hours: number of hours needed for the task (in 24H format) without specifying start/end time (Required Field if start/end are not specified)

## Request Body (JSON)
{
    "tasks":[
        {"day": "Sunday", "name": "Assignment", "start": 10, "end": 14, "priority": 2},
        {"day": "Saturday", "name": "Shopping", "hours": 4, "priority": 3}
    ]
}

## Response Body (JSON)
{
    "saturday": [
        {
            "name": "Shopping",
            "start": 0,
            "end": 4
        }
    ],
    "sunday": [
        {
            "name": "Assignment",
            "start": 10,
            "end": 14
        }
    ],
    "monday": [],
    "tuesday": [],
    "wednesday": [],
    "thursday": [],
    "friday": []
}

## Scheduler Policy
1) Tasks with higher priority should be added first
2) Tasks with same priority and have conflict (time overlapping): the scheduler will choose the task which maximize the number of added task through the day using “Earliest Finish Time Algorithm”.
3) Tasks with unspecified time boundary (just hours) have lower priority than the “From/To” tasks and will be fitted in the schedule to maximize the number of added Items (Shortest task first in the shortest free slot).