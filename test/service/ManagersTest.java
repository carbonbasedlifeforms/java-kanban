package service;

import com.yandex.kanbanboard.service.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Объект менеджера задач должен существовать");
        assertInstanceOf(InMemoryTaskManager.class, manager, "Класс объекта: InMemoryTaskManager");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history, "Объект менеджера истории задач должен существовать");
        assertInstanceOf(InMemoryHistoryManager.class, history, "Класс объекта: InMemoryHistoryManager");
    }

    @Test
    void getDefaultFileBackedTaskManager() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(Files.createTempFile("test", "ext").toFile());
        assertNotNull(fileBackedTaskManager,
                "Объект менеджера задач с сохранением в файл должен существовать");
        assertInstanceOf(FileBackedTaskManager.class, fileBackedTaskManager, "Класс объекта: InMemoryTaskManager");
    }
}