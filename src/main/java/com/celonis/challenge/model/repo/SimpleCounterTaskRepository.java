package com.celonis.challenge.model.repo;

import com.celonis.challenge.model.SimpleCounterTask;
import com.celonis.challenge.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SimpleCounterTaskRepository extends JpaRepository<SimpleCounterTask, String> {

    List<SimpleCounterTask> findByStatusInAndCreationDateLessThan(List<TaskStatus> status, Date date);
}
