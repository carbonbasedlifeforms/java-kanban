package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    public final int HISTORY_LIMIT = 10;
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_LIMIT) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
