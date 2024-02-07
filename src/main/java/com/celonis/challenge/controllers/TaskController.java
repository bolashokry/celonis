package com.celonis.challenge.controllers;

import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskWrapper;
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

    /*
    * That part of "keep existing behavior and API" in the requirement document has confused me a little.
    * For example, I hesitated should I use the same end point for creating both task types or to separate end point for each.
    * Finally, I opted to use the same endpoint and differentiate between task types by a new field "type" in the request body.
    * I had also to modify the request body type and sometimes the return type of some methods to match my decision.
    *  */

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public List<TaskWrapper> listTasks() {
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

    @PostMapping("/{taskId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTask(@PathVariable String taskId) {
        taskService.cancelTask(taskId);
    }

}
