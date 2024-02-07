package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProjectGenerationTask.class, name = "PROJECT_GENERATION"),
        @JsonSubTypes.Type(value = SimpleCounterTask.class, name = "SIMPLE_COUNTER")})
public abstract class Task {
    @javax.persistence.Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private Date creationDate;

    private TaskType type;

    private TaskStatus status;
}
