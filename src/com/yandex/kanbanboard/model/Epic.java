package com.yandex.kanbanboard.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    //привязанные к эпику ID сабтасок хранятся в его списке
    private final List<Integer> epicSubtasksIds = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getEpicSubtasksIds() {
        return epicSubtasksIds;
    }
    // при создании сабтаски добавляем эту задачу в эпик в таскменеджере
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
    public String toString() {
        return super.toString() +
                " epicSubtasksIds=" + epicSubtasksIds;
    }
}

