package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.repo.ProjectGenerationTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;

@Component
@Slf4j
public class FileService {
    /* TODO this class needs to be refactored, maybe extracting files-related activities to a separate util class
            and move all tasks persistence and loading to ProjectGenerationTaskProcessor */

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;

    public FileService(ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    public String getTaskResult(String taskId) {
        log.info("Getting task {} result", taskId);
        ProjectGenerationTask projectGenerationTask = projectGenerationTaskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);
        File inputFile = new File(projectGenerationTask.getStorageLocation() == null ?
                "" : projectGenerationTask.getStorageLocation());

        if (!inputFile.exists()) {
            throw new InternalException("File not generated yet");
        }

        try {
            return new String(new FileSystemResource(inputFile).getInputStream().readAllBytes());
        } catch (IOException ex) {
            throw new InternalException("Couldn't read file contents");
        }
    }

    public void storeResult(String taskId, URL url) throws IOException {
        log.info("Storing result of task: {}", taskId);
        ProjectGenerationTask projectGenerationTask = projectGenerationTaskRepository.findById(taskId)
                .orElseThrow(NotFoundException::new);
        File outputFile = File.createTempFile(taskId, ".zip");
        outputFile.deleteOnExit();
        projectGenerationTask.setStorageLocation(outputFile.getAbsolutePath());
        projectGenerationTaskRepository.save(projectGenerationTask);
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }
}
