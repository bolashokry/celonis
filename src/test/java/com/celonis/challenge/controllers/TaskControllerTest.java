package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.SimpleCounterTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskType;
import com.celonis.challenge.model.repo.ProjectGenerationTaskRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

    @Value("http://localhost:${local.server.port}/api/tasks/")
    private String localhostBaseUrl;

    private final String HEADER_NAME = "Celonis-Auth";
    private final String HEADER_VALUE = "totally_secret";

    @Autowired
    private ProjectGenerationTaskRepository projectGenerationTaskRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate
                .getRestTemplate()
                .setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
    }

    // TODO add test cases for wrong scenarios, for example, delete or get not existing tasks

    @Test
    void shouldSaveProjectGenerationTask() {
        // TODO create helper method for tasks creation
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName("test task");
        task.setType(TaskType.PROJECT_GENERATION);
        ResponseEntity<ProjectGenerationTask> response = createTask(task, ProjectGenerationTask.class);

        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody().getName(), equalTo("test task"));
        MatcherAssert.assertThat(response.getBody().getCreationDate(), notNullValue());
    }

    @Test
    void shouldSaveSimpleCounterTask() {
        SimpleCounterTask task = new SimpleCounterTask();
        task.setName("test task");
        task.setType(TaskType.SIMPLE_COUNTER);
        task.setX(10);
        task.setY(20);
        ResponseEntity<SimpleCounterTask> response = createTask(task, SimpleCounterTask.class);

        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody().getName(), equalTo("test task"));
        MatcherAssert.assertThat(response.getBody().getX(), equalTo(10));
        MatcherAssert.assertThat(response.getBody().getY(), equalTo(20));
    }

    @Test
    public void shouldGetProjectGenerationTask() {
        // given
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName("test task");
        task.setType(TaskType.PROJECT_GENERATION);
        String savedTaskId = Objects.requireNonNull(createTask(task, ProjectGenerationTask.class).getBody()).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);

        // when
        ResponseEntity<ProjectGenerationTask> response =  restTemplate.exchange(
                localhostBaseUrl + savedTaskId, HttpMethod.GET, new HttpEntity<>(headers),
                ProjectGenerationTask.class);

        // then
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody().getId(), equalTo(savedTaskId));
    }

    @Test
    public void shouldUpdateProjectGenerationTask() {
        // given
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName("test task");
        task.setType(TaskType.PROJECT_GENERATION);
        String savedTaskId = Objects.requireNonNull(createTask(task, ProjectGenerationTask.class).getBody()).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);

        // when
        task.setName("Updated name");
        HttpEntity<ProjectGenerationTask> request = new HttpEntity<>(task, headers);
        this.restTemplate.put(localhostBaseUrl + savedTaskId, request);

        // then
        ResponseEntity<ProjectGenerationTask> response =  restTemplate.exchange(
                localhostBaseUrl + savedTaskId, HttpMethod.GET, new HttpEntity<>(headers),
                ProjectGenerationTask.class);
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody().getName(), equalTo("Updated name"));
    }

    @Test
    public void shouldDeleteProjectGenerationTask() {
        // given
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName("test task");
        task.setType(TaskType.PROJECT_GENERATION);
        String savedTaskId = Objects.requireNonNull(createTask(task, ProjectGenerationTask.class).getBody()).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);

        // when
        this.restTemplate.exchange(
                localhostBaseUrl + savedTaskId, HttpMethod.DELETE, new HttpEntity<>(headers), ProjectGenerationTask.class);

        // then
        ResponseEntity<Void> response =  restTemplate.exchange(
                localhostBaseUrl + savedTaskId, HttpMethod.GET, new HttpEntity<>(headers),
                Void.class);
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    private <T extends Task> ResponseEntity<T> createTask(T task, Class<T> taskType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);

        HttpEntity<T> request = new HttpEntity<>(task, headers);
        return this.restTemplate.postForEntity(localhostBaseUrl, request, taskType);
    }
}