package com.yandex.kanbanboard.model;

import java.util.ArrayList;

public class Epic extends Task {
    //привязанные к эпику ID сабтасок хранятся в его списке
    private final ArrayList<Integer> epicSubtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getEpicSubtasksIds() {
        return epicSubtasksIds;
    }
    // при создании сабтаски добавляем эту задачу в эпик в таскменеджере
    public void addSubTaskToEpic(int id) {
        epicSubtasksIds.add(id);
    }

    @Override
    public String toString() {
        return super.toString() +
                " epicSubtasksIds=" + epicSubtasksIds;
    }

}

