package com.celonis.challenge.services.processors;

import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.SimpleCounterTask;
import com.celonis.challenge.model.TaskWrapper;
import com.celonis.challenge.model.repo.SimpleCounterTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.celonis.challenge.model.TaskStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleCounterTaskProcessor implements TaskProcessor {

    private final SimpleCounterTaskRepository simpleCounterTaskRepository;

    /*
        This map is used to do the in-memory second-based increments. The map is reflected in the DB once the task is
            completed or cancelled. This way I can avoid hitting the DB every second for evdery task.
        This could have been implemented in other different ways.
        1- Do not do real increments at all, and rely on calculating the current progress at run-time by
            subtracting now - task creation date. This is the most efficient solution, but I didn't implement it this way
            because I don't think you wanted it like this.
        2- Hit the database every second to avoid any data lose, but this comes on the cost of performance.

        In a real microservice application I'd suggest implementing this using distributed in-memory DB (i.e. redis)
     */
    private final Map<String, SimpleCounterTask> inProgressTasks = new HashMap<>();
    private final static int PROGRESS_STEP_IN_MILLIS = 1000;

    @Override
    public SimpleCounterTask save(TaskWrapper taskWrapper) {
        return simpleCounterTaskRepository.save((SimpleCounterTask) taskWrapper.getTask());
    }

    @Override
    public Optional<SimpleCounterTask> load(String taskId) {
        return simpleCounterTaskRepository.findById(taskId);
    }

    @Override
    public void delete(String taskId) {
        simpleCounterTaskRepository.deleteById(taskId);
    }

    @Override
    public void execute(String taskId) {
        if (inProgressTasks.containsKey(taskId)) {
            log.info("Task {} is already in progress", taskId);
            return;
        }

        load(taskId)
                .filter(task -> NEW.equals(task.getStatus()))
                .ifPresentOrElse(task -> {
                    /*
                    The sync between inProgressTasks map and the DB could have been done better than this
                        but again, the time constraint
                     */
                    inProgressTasks.put(taskId, task);
                    task.setStatus(IN_PROGRESS);
                    simpleCounterTaskRepository.save(task);
                    CompletableFuture.runAsync(() -> {
                        task.setProgress(task.getX());
                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TaskCounter(taskId), 0, PROGRESS_STEP_IN_MILLIS);
                    });
                }, NotFoundException::new);
    }

    @Override
    public String getResult(String taskId) {
        SimpleCounterTask savedTask = load(taskId).orElseThrow(NotFoundException::new);
        SimpleCounterTask inProgressTask = inProgressTasks.get(taskId);
        if (inProgressTask != null) {
            savedTask.setProgress(inProgressTask.getProgress());
        }
        return toResultString(savedTask);
    }

    @Override
    public void cancel(String taskId) {
        load(taskId)
                .filter(task -> List.of(NEW, IN_PROGRESS).contains(task.getStatus()))
                .ifPresentOrElse(task -> {
                    task.setStatus(CANCELLED);
                    SimpleCounterTask inProgressTask = inProgressTasks.get(taskId);
                    if (inProgressTask != null) {
                        task.setProgress(inProgressTask.getProgress());
                    }
                    simpleCounterTaskRepository.save(task);
                }, () -> log.info("Can't find task {}, or it is not cancellable", taskId));
    }

    private String toResultString(SimpleCounterTask task) {
        return new StringBuilder()
                .append("Name: ")
                .append(task.getName())
                .append(", X: ")
                .append(task.getX())
                .append(", Y: ")
                .append(task.getY())
                .append(", Progress: ")
                .append(task.getProgress())
                .append(", Status: ")
                .append(task.getStatus())
                .toString();

    }

    private class TaskCounter extends TimerTask {
        private final String taskId;

        TaskCounter(String taskId) {
            this.taskId = taskId;
        }

        @Override
        public void run() {
            final SimpleCounterTask inProgressTask = inProgressTasks.get(taskId);
            inProgressTask.incrementProgress();
            log.debug("Progress of task {} is {}", taskId, inProgressTask.getProgress());
            if (inProgressTask.getProgress() == inProgressTask.getY()) {
                log.info("Task {} has completed!", taskId);
                inProgressTasks.remove(taskId);
                load(taskId).ifPresentOrElse(task -> {
                    task.setStatus(COMPLETED);
                    task.setProgress(inProgressTask.getY());
                    simpleCounterTaskRepository.save(task);

                }, () -> log.error("Can't find task {}", taskId));
                this.cancel();
            }
        }
    }
}
