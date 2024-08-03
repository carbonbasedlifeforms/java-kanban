package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
