package com.celonis.challenge.model;

public enum TaskStatus {

    /** The task has been created but never executed. */
    NEW,

    /** The task is executed and currently is in progress. */
    IN_PROGRESS,

    /** The task execution has completed */
    COMPLETED,

    /** The has been cancelled. Only NEW or IN_PROGRESS tasks could be cancelled. */
    CANCELLED
}
