package com.celonis.challenge.services.processors;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskWrapper;
import com.celonis.challenge.model.repo.ProjectGenerationTaskRepository;
import com.celonis.challenge.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectGenerationTaskProcessor implements TaskProcessor {

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;
    private final FileService fileService;

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

    @Override
    public void execute(String taskId) {
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

    @Override
    public String getResult(String taskId) {
        return fileService.getTaskResult(taskId);
    }

    @Override
    public void cancel(String taskId) {
        log.info("Project generation tasks can't be cancelled");
    }
}
