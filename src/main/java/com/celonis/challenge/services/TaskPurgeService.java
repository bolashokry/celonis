package com.celonis.challenge.services;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.SimpleCounterTask;
import com.celonis.challenge.model.repo.ProjectGenerationTaskRepository;
import com.celonis.challenge.model.repo.SimpleCounterTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.celonis.challenge.model.TaskStatus.NEW;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableAsync
@EnableScheduling
public class TaskPurgeService {

    @Value("${task.purge.threshold-in-seconds}")
    private int purgeThresholdInSeconds;

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;
    private final SimpleCounterTaskRepository simpleCounterTaskRepository;


    /*
        In a real microservice application, I'd use cloud-based event (i.e. cloud watch) rather than
        spring boot's schedule
     */
    @Async
    @Scheduled(fixedRateString = "${task.purge.frequency-in-milliseconds}")
    public void purgeOutDatedTasks() {
        log.info("Purging the too old tasks..");

        /*
        There is no need to load the to-be-deleted entities then delete them. They could be deleted directly
            using the repository, but I did it this way just for logging purpose.
        */

        final List<ProjectGenerationTask> toBeDeletedProjectGenerationTasks = projectGenerationTaskRepository
                .findByStatusInAndCreationDateLessThan(List.of(NEW),
                        DateUtils.addSeconds(new Date(), -purgeThresholdInSeconds));

        final List<SimpleCounterTask> toBeDeletedSimpleCounterTasks = simpleCounterTaskRepository
                .findByStatusInAndCreationDateLessThan(List.of(NEW),
                        DateUtils.addSeconds(new Date(), -purgeThresholdInSeconds));

        log.info("The following {} too old tasks will be deleted {} {}",
                toBeDeletedProjectGenerationTasks.size() + toBeDeletedSimpleCounterTasks.size(),
                toBeDeletedProjectGenerationTasks, toBeDeletedSimpleCounterTasks);

        projectGenerationTaskRepository.deleteInBatch(toBeDeletedProjectGenerationTasks);
        simpleCounterTaskRepository.deleteInBatch(toBeDeletedSimpleCounterTasks);

    }
}
