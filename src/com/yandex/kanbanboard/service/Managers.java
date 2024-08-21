package com.yandex.kanbanboard.service;

import java.io.File;


public class Managers extends InMemoryTaskManager {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }
}
