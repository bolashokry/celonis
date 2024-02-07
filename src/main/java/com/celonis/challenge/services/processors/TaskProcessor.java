package com.celonis.challenge.services.processors;

import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskWrapper;

import java.util.Optional;

public interface TaskProcessor {

    Task save(TaskWrapper taskWrapper);

    Optional<? extends Task> load(String taskId);

    void delete(String taskId);

    void execute(String taskId);

    String getResult(String taskId);

    void cancel(String taskId);
}
