package com.yandex.kanbanboard.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    //привязанные к эпику ID сабтасок хранятся в его списке
    private final List<Integer> epicSubtasksIds = new ArrayList<>();
    private final TaskTypes taskType = TaskTypes.EPIC;

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getEpicSubtasksIds() {
        return epicSubtasksIds;
    }

    public void addSubTaskToEpic(int id) {
        epicSubtasksIds.add(id);
    }

    public void deleteSubTask(Integer subtaskId) {
        epicSubtasksIds.remove(subtaskId);
    }

    public void clearAllEpicSubtasks() {
        epicSubtasksIds.clear();
        this.setStatus(TaskStatus.NEW);
    }

    @Override
    public TaskTypes getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return super.toString() +
                " epicSubtasksIds=" + epicSubtasksIds;
    }
}

