package com.celonis.challenge.services.processors;

import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.repo.ProjectGenerationTaskRepository;
import com.celonis.challenge.model.repo.SimpleCounterTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessorResolver {

    private final ProjectGenerationTaskProcessor projectGenerationTaskProcessor;
    private final SimpleCounterTaskProcessor simpleCounterTaskProcessor;
    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;
    private final SimpleCounterTaskRepository simpleCounterTaskRepository;

    public TaskProcessor resolve(Task task) {
        switch (task.getType()) {
            case PROJECT_GENERATION:
                return projectGenerationTaskProcessor;
            case SIMPLE_COUNTER:
                return simpleCounterTaskProcessor;
            default:
                throw new IllegalArgumentException("Unexpected task type");
        }
    }

    /* Better solution here is to create a separate table of <task_id and task_type> to be able to resolve
        task type directly by id without the need to check its existence in every task-type table.
        This solution has been omitted due to time constrains.
     */
    public TaskProcessor resolveById(String id) {
        if (projectGenerationTaskRepository.existsById(id)) {
            return projectGenerationTaskProcessor;
        } else if (simpleCounterTaskRepository.existsById(id)) {
            return simpleCounterTaskProcessor;
        }
        throw new NotFoundException();

    }
}
