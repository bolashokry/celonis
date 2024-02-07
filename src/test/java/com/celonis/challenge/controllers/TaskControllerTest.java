package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.SimpleCounterTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskType;
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

import java.util.List;
import java.util.Objects;

import static com.celonis.challenge.model.TaskStatus.NEW;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

    @Value("http://localhost:${local.server.port}/api/tasks/")
    private String localhostBaseUrl;

    private final String HEADER_NAME = "Celonis-Auth";
    private final String HEADER_VALUE = "totally_secret";

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate
                .getRestTemplate()
                .setMessageConverters(List.of(new MappingJackson2HttpMessageConverter(), new StringHttpMessageConverter()));
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
        Thread.sleep(3500);
        ResponseEntity<String> secondResponse = restTemplate.exchange(
                localhostBaseUrl + savedTaskId + "/result", HttpMethod.GET, new HttpEntity<>(headers),
                String.class);

        // then
        MatcherAssert.assertThat(secondResponse.getStatusCode(), equalTo(HttpStatus.OK));
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.notNullValue());
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.containsString("Progress: 12"));
        MatcherAssert.assertThat(secondResponse.getBody(), Matchers.containsString("Status: COMPLETED"));

    }

    private <T extends Task> ResponseEntity<T> createTask(T task, Class<T> taskType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_NAME, HEADER_VALUE);

        HttpEntity<T> request = new HttpEntity<>(task, headers);
        return this.restTemplate.postForEntity(localhostBaseUrl, request, taskType);
    }
}