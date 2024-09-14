package com.yandex.kanbanboard.model;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;
    private final TaskTypes taskType = TaskTypes.SUBTASK;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId,
                   int duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus status, int epicId,
                   int duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskTypes getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return super.toString() +
                " epicId=" + epicId;
    }
}
