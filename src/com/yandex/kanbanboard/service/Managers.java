package com.yandex.kanbanboard.service;

import java.io.File;


public class Managers extends InMemoryTaskManager {
    private static File file;

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedTaskManager() {
        return new FileBackedTaskManager(file);
    }

    public static void setFile(File file) {
        Managers.file = file;
    }
}
