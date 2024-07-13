package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();

}
