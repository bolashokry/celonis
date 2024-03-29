package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.SimpleCounterTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskType;
import com.celonis.challenge.model.repo.SimpleCounterTaskRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static com.celonis.challenge.model.TaskStatus.NEW;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class TaskControllerTest {

    /*
        Due to the time constrains, only the happy scenarios are covered here.
     */

    @Value("http://localhost:${local.server.port}/api/tasks/")
    private String localhostBaseUrl;

    private final String HEADER_NAME = "Celonis-Auth";
    private final String HEADER_VALUE = "totally_secret";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SimpleCounterTaskRepository simpleCounterTaskRepository;

    @BeforeEach
    void setUp() {
        restTemplate
                .getRestTemplate()
                .setMessageConverters(List.of(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter()));
    }

    // TODO create helper method for tasks creation, request preparations, etc..

    @Test
    void shouldSaveProjectGenerationTask() {
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName("test task");
        task.setType(TaskType.PROJECT_GENERATION);
        ResponseEntity<ProjectGenerationTask> response = createTask(task, ProjectGenerationTask.class);

        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody().getName(), equalTo("test task"));
        MatcherAssert.assertThat(response.getBody().getCreationDate(), notNullValue());
        MatcherAssert.assertThat(response.getBody().getStatus(), equalTo(NEW));
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
        MatcherAssert.assertThat(response.getBody().getStatus(), equalTo(NEW));
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
        ResponseEntity<ProjectGenerationTask> response = restTemplate.exchange(
                localhostBaseUrl + savedTaskId, HttpMethod.GET, new HttpEntity<>(headers),
                ProjectGenerationTask.class);

        // then
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody().getId(), equalTo(savedTaskId));
        MatcherAssert.assertThat(response.getBody().getStatus(), equalTo(NEW));
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
        ResponseEntity<ProjectGenerationTask> response = restTemplate.exchange(
                localhostBaseUrl + savedTaskId, HttpMethod.GET, new HttpEntity<>(headers),
                ProjectGenerationTask.class);
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody().getName(), equalTo("Updated name"));
        MatcherAssert.assertThat(response.getBody().getStatus(), equalTo(NEW));
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
        ResponseEntity<Void> response = restTemplate.exchange(
                localhostBaseUrl + savedTaskId, HttpMethod.GET, new HttpEntity<>(headers),
                Void.class);
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void shouldExecuteProjectGenerationTask() {
        // given
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName("test task");
        task.setType(TaskType.PROJECT_GENERATION);
        String savedTaskId = Objects.requireNonNull(createTask(task, ProjectGenerationTask.class).getBody()).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);

        // when
        ResponseEntity<Void> response = restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/execute", HttpMethod.POST, new HttpEntity<>(headers),
                Void.class);

        // then
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    @Test
    public void shouldGetResultOfProjectGenerationTask() {
        // given
        ProjectGenerationTask task = new ProjectGenerationTask();
        task.setName("test task");
        task.setType(TaskType.PROJECT_GENERATION);
        String savedTaskId = Objects.requireNonNull(createTask(task, ProjectGenerationTask.class).getBody()).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);
        restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/execute", HttpMethod.POST, new HttpEntity<>(headers),
                Void.class);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/result", HttpMethod.GET, new HttpEntity<>(headers),
                String.class);

        // then
        MatcherAssert.assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(response.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(response.getBody(), equalTo("Hello World!"));
    }

    @Test
    public void shouldExecuteAndGetResultOfSimpleCounterTask() throws InterruptedException {
        // given
        SimpleCounterTask task = new SimpleCounterTask();
        task.setName("test task");
        task.setType(TaskType.SIMPLE_COUNTER);
        task.setX(10);
        task.setY(12);
        String savedTaskId = Objects.requireNonNull(createTask(task, SimpleCounterTask.class).getBody()).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);
        restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/execute", HttpMethod.POST, new HttpEntity<>(headers),
                Void.class);

        // when
        ResponseEntity<String> firstResponse = restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/result", HttpMethod.GET, new HttpEntity<>(headers),
                String.class);

        // then
        MatcherAssert.assertThat(firstResponse.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(firstResponse.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(firstResponse.getBody(), Matchers.containsString("Progress:"));
        MatcherAssert.assertThat(firstResponse.getBody(), Matchers.containsString("Status: IN_PROGRESS"));

        // when
        Thread.sleep(2500);
        ResponseEntity<String> secondResponse = restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/result", HttpMethod.GET, new HttpEntity<>(headers),
                String.class);

        // then
        MatcherAssert.assertThat(secondResponse.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.containsString("Progress: 12"));
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.containsString("Status: COMPLETED"));
    }

    @Test
    public void shouldCancelInProgressTasks() throws InterruptedException {
        // given
        SimpleCounterTask task = new SimpleCounterTask();
        task.setName("test task");
        task.setType(TaskType.SIMPLE_COUNTER);
        task.setX(10);
        task.setY(15);
        String savedTaskId = Objects.requireNonNull(createTask(task, SimpleCounterTask.class).getBody()).getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);
        restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/execute", HttpMethod.POST, new HttpEntity<>(headers),
                Void.class);
        restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/cancel", HttpMethod.POST, new HttpEntity<>(headers),
                Void.class);

        // when
        Thread.sleep(1000);
        ResponseEntity<String> secondResponse = restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/result", HttpMethod.GET, new HttpEntity<>(headers),
                String.class);

        // then
        MatcherAssert.assertThat(secondResponse.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.containsString("Status: CANCELLED"));
    }

    @Test
    public void shouldPurgeOldTasks() throws InterruptedException {
        // given
        SimpleCounterTask task1 = new SimpleCounterTask();
        task1.setName("test task");
        task1.setType(TaskType.SIMPLE_COUNTER);
        task1.setX(10);
        task1.setY(15);
        String savedTask1Id = Objects.requireNonNull(createTask(task1, SimpleCounterTask.class).getBody()).getId();

        Thread.sleep(2500);
        // given
        SimpleCounterTask task2 = new SimpleCounterTask();
        task2.setName("test task");
        task2.setType(TaskType.SIMPLE_COUNTER);
        task2.setX(10);
        task2.setY(15);
        String savedTask2Id = Objects.requireNonNull(createTask(task2, SimpleCounterTask.class).getBody()).getId();

        List<SimpleCounterTask> tasks = simpleCounterTaskRepository.findAll();
        MatcherAssert.assertThat(tasks.size(), equalTo(1));
        MatcherAssert.assertThat(tasks.get(0).getId(), equalTo(savedTask2Id));
    }

    private <T extends Task> ResponseEntity<T> createTask(T task, Class<T> taskType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);

        HttpEntity<T> request = new HttpEntity<>(task, headers);
        return this.restTemplate.postForEntity(localhostBaseUrl, request, taskType);
    }
}