package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskWrapper;
import com.celonis.challenge.services.processors.ProcessorResolver;
import com.celonis.challenge.services.processors.TaskProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.celonis.challenge.model.TaskStatus.NEW;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final ProcessorResolver processorResolver;

    public List<ProjectGenerationTask> listTasks() {
        log.info("List all tasks");
        throw new UnsupportedOperationException("to be implemented");
    }

    public Task createTask(Task task) {
        log.info("Creating task: {}", task);
        task.setId(null);
        task.setCreationDate(new Date());
        task.setStatus(NEW);
        return getProcessor(task).save(new TaskWrapper(task));
    }

    public Task getTask(String taskId) {
        log.info("Getting task: {}", taskId);
        return getProcessor(taskId).load(taskId).orElseThrow(NotFoundException::new);
    }

    public Task update(String taskId, Task task) {
        log.info("Updating task: {} with value: {}", taskId, task);
        Task existingTask = getTask(taskId);
        existingTask.setCreationDate(task.getCreationDate());
        existingTask.setName(task.getName());
        return getProcessor(existingTask).save(new TaskWrapper(task));
    }

    public void delete(String taskId) {
        log.info("Deleting task: {}", taskId);
        getProcessor(taskId).delete(taskId);
    }

    public void executeTask(String taskId) {
        log.info("Executing task: {}", taskId);
        getProcessor(taskId).execute(taskId);
    }

    public String getResult(String taskId) {
        log.info("Getting result of task: {}", taskId);
        return getProcessor(taskId).getResult(taskId);
    }

    private TaskProcessor getProcessor(Task task) {
        return processorResolver.resolve(task);
    }

    private TaskProcessor getProcessor(String taskId) {
        return processorResolver.resolveById(taskId);
    }

}
