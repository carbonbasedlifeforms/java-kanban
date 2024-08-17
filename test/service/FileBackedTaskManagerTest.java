package service;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.service.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    static File file;
    FileBackedTaskManager fileManager;

    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        try {
            file = File.createTempFile("testFile", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadFromEmptyFile() {
        fileManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(fileManager.getAll().isEmpty(), "Не пустой список всех типов задач, при загрузке пустого файла");
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager initFileManager = FileBackedTaskManager.loadFromFile(file);
        fileManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(fileManager.getAll().isEmpty(), "Не пустой список всех типов задач, при загрузке пустого файла");

        task = new Task("Task 1", "Description task 1");
        initFileManager.createTask(task);
        epic = new Epic("Epic 1", "Epic epic 1");
        initFileManager.createEpic(epic);
        subtask = new Subtask("SubTask 1", "Description subtask 1", epic.getId());
        initFileManager.createSubtask(subtask);

        fileManager = FileBackedTaskManager.loadFromFile(file);
        assertFalse(fileManager.getAllTasks().isEmpty(), "Пустой список задач, при загрузке не пустого файла");
    }

}