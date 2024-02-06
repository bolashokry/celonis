package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
public class ProjectGenerationTask extends Task {

    @JsonIgnore
    private String storageLocation;

}
