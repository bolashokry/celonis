package com.celonis.challenge.model.repo;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProjectGenerationTaskRepository extends JpaRepository<ProjectGenerationTask, String> {

    List<ProjectGenerationTask> findByStatusInAndCreationDateLessThan(List<TaskStatus> status, Date date);
}
