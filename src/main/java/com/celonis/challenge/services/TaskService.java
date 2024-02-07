package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskWrapper;
import com.celonis.challenge.services.processors.ProcessorResolver;
import com.celonis.challenge.services.processors.TaskProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final ProcessorResolver processorResolver;

    private final FileService fileService;

    public List<ProjectGenerationTask> listTasks() {
        log.info("List all tasks");
        throw new UnsupportedOperationException("to be implemented");
    }

    public Task createTask(Task task) {
        log.info("Creating task: {}", task);
        task.setId(null);
        task.setCreationDate(new Date());
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
        URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new InternalException("Zip file not found");
        }
        try {
            fileService.storeResult(taskId, url);
        } catch (Exception e) {
            throw new InternalException(e);
        }
    }

    private TaskProcessor getProcessor(Task task) {
        return processorResolver.resolve(task);
    }

    private TaskProcessor getProcessor(String taskId) {
        return processorResolver.resolveById(taskId);
    }

}
