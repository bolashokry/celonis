package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
@JsonTypeName("SIMPLE_COUNTER")
public class SimpleCounterTask extends Task {
    private int x;
    private int y;
    private int progress;

    public void incrementProgress() {
        progress++;
    }
}
