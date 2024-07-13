package com.yandex.kanbanboard.service;

public class Managers extends InMemoryTaskManager{

    public static TaskManager getDefault() {
        TaskManager manager = new InMemoryTaskManager();
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}
