package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
@JsonTypeName("PROJECT_GENERATION")
public class ProjectGenerationTask extends Task {

    @JsonIgnore
    private String storageLocation;

}
