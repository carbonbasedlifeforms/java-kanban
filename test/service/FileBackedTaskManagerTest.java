package service;

import com.yandex.kanbanboard.exceptions.ValidationException;
import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskStatus;
import com.yandex.kanbanboard.service.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    static File file;
    FileBackedTaskManager fileManager;
    FileBackedTaskManager initFileManager;

    Task task;
    Epic epic;
    Subtask subtask;
    final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void beforeEach() throws IOException {
        try {
            file = File.createTempFile("testFile", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        task = new Task("Task 1", "Description task 1", TaskStatus.NEW, 100,
                LocalDateTime.parse("2020-01-01 00:00:00", dateFormat));
        epic = new Epic("Epic 1", "Epic epic 1");
        initFileManager = FileBackedTaskManager.loadFromFile(file);
        fileManager = FileBackedTaskManager.loadFromFile(file);
    }

    @Test
    void loadFromEmptyFile() {
        assertTrue(fileManager.getAll().isEmpty(),
                "Не пустой список всех типов задач, при загрузке пустого файла");
    }

    @Test
    void loadFromFile() throws IOException {
        assertTrue(fileManager.getAll().isEmpty(),
                "Не пустой список всех типов задач, при загрузке пустого файла");
        initFileManager.createTask(task);
        initFileManager.createEpic(epic);
        fileManager = FileBackedTaskManager.loadFromFile(file);
        assertFalse(fileManager.getAllTasks().isEmpty(), "Пустой список задач, при загрузке не пустого файла");
    }

    @Test
    void testValidationException() {
        initFileManager.createTask(task);
        assertThrows(ValidationException.class, () -> initFileManager.createTask(task),
                "Нет выбрасывается ValidationException");
    }
}