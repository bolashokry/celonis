package com.celonis.challenge.services.processors;

import com.celonis.challenge.model.SimpleCounterTask;
import com.celonis.challenge.model.TaskWrapper;
import com.celonis.challenge.model.repo.SimpleCounterTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleCounterTaskProcessor implements TaskProcessor {

    private final SimpleCounterTaskRepository simpleCounterTaskRepository;

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
}
