package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Data
@ToString(callSuper = true)
@JsonTypeName("SIMPLE_COUNTER")
public class SimpleCounterTask extends Task {
    private int x;
    private int y;
}
