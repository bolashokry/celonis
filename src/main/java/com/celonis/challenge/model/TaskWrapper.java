package com.celonis.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/*
   I needed this wrapper for 2 purposes.
   First, to be able to combine both task types into one response list of "list tasks" end point.
   Secondly, to be able to isolate the not-so-good down-casting in task's processor level and not do it in a generic place.
   Ideally, I'd have either created 2 different endpoints one for each task type, or, if we might have undefined
        number of task types, I'd not have used polymorphism, but instead only 1 task class with json payload.
 */

@Data
@AllArgsConstructor
public class TaskWrapper {
    private Task task;
}
