package com.TaskScheduler.Services.Manager;

import com.TaskScheduler.Exceptions.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class ClientInputValidation {

    public ClientInputValidation() {
    }

    public void validateInput(ClientRequestBody reqBody) throws BadRequestException {
        Iterator reqBodyIter = reqBody.getTasks().iterator();
        while (reqBodyIter.hasNext()){
            ClientRequestTask currentTask = (ClientRequestTask) reqBodyIter.next();

            if(currentTask.getDay() == null || !(currentTask.getDay() instanceof String)){
                throw new BadRequestException("Day field is required as a String");
            }

            if(currentTask.getName() == null || !(currentTask.getName()instanceof String)){
                throw new BadRequestException("Name field is required as a String");
            }

            if(currentTask.getStart() == 0 && currentTask.getEnd() == 0 && currentTask.getHours() == 0){
                throw new BadRequestException("(Start and End) fields OR Hours field should be existed");
            }

            if((currentTask.getStart() != 0 || currentTask.getEnd() != 0) && currentTask.getHours() != 0){
                throw new BadRequestException("At most (Start and End) fields OR Hours field should be existed not both");
            }

            if(currentTask.getStart() < 0 ||
            currentTask.getStart() > 24 ||
            currentTask.getEnd() <0 ||
            currentTask.getEnd() > 24 ||
            currentTask.getHours() < 0 ||
            currentTask.getHours() > 24){
                throw new BadRequestException("(Start and End) or Hours fields should be between 0 and 24");
            }

            if((currentTask.getEnd() <= currentTask.getStart()) && (currentTask.getStart() != 0 || currentTask.getEnd() != 0)){
                throw new BadRequestException("End field should be greater than Start field");
            }

            if(currentTask.getPriority() <= 0 || currentTask.getPriority() > 3){
                throw new BadRequestException("Priority field should be existed and ranges between 1 and 3");
            }

            if (! checkDayString(currentTask.getDay())){
                throw new BadRequestException("day field should only contain one of these values: \"Everyday\", \"Saturday\", \"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\" ");
            }
        }
    }

    private boolean checkDayString(String dayCandidate){
        boolean flag = false;
        String[] days = {"Everyday", "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String day : days){
            if (dayCandidate.equals(day)){
                flag = true;
            }
        }
        return flag;
    }
}
