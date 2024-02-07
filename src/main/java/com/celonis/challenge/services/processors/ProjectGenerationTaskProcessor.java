package com.celonis.challenge.services.processors;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskWrapper;
import com.celonis.challenge.model.repo.ProjectGenerationTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectGenerationTaskProcessor implements TaskProcessor {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    @Override
    public ProjectGenerationTask save(TaskWrapper taskWrapper) {
        return projectGenerationTaskRepository.save((ProjectGenerationTask) taskWrapper.getTask());
    }

    @Override
    public Optional<ProjectGenerationTask> load(String taskId) {
        return projectGenerationTaskRepository.findById(taskId);
    }

    @Override
    public void delete(String taskId) {
        projectGenerationTaskRepository.deleteById(taskId);
    }
}
