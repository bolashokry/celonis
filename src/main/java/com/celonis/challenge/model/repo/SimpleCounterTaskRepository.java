package com.celonis.challenge.model.repo;

import com.celonis.challenge.model.SimpleCounterTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleCounterTaskRepository extends JpaRepository<SimpleCounterTask, String> {
}
