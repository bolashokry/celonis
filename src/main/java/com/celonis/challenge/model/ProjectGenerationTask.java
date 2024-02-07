package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
@JsonTypeName("PROJECT_GENERATION")
public class ProjectGenerationTask extends Task {

    @JsonIgnore
    private String storageLocation;

}
