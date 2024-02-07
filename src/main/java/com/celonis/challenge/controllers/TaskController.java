package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.TaskService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService,
                          FileService fileService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public List<ProjectGenerationTask> listTasks() {
        return taskService.listTasks();
    }

    @PostMapping("/")
    public Task createTask(@RequestBody @Valid Task task) {
        return taskService.createTask(task);
    }

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable String taskId,
                           @RequestBody @Valid Task projectGenerationTask) {
        return taskService.update(taskId, projectGenerationTask);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        taskService.delete(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executeTask(@PathVariable String taskId) {
        taskService.executeTask(taskId);
    }

    @GetMapping("/{taskId}/result")
    public ResponseEntity<String> getResult(@PathVariable String taskId) {
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>(taskService.getResult(taskId), respHeaders, HttpStatus.OK);
    }

}
