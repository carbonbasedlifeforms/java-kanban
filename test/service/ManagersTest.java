package service;

import com.yandex.kanbanboard.service.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManagersTest {
    @Test
    void getDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Объект менеджера задач должен существовать");
        assertTrue(manager instanceof InMemoryTaskManager, "Класс объекта: InMemoryTaskManager");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history, "Объект менеджера истории задач должен существовать");
        assertTrue(history instanceof InMemoryHistoryManager, "Класс объекта: InMemoryHistoryManager");
    }

    @Test
    void getDefaultFileBackedTaskManager() {
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager();
        assertNotNull(fileBackedTaskManager,
                "Объект менеджера задач с сохранением в файл должен существовать");
        assertTrue(fileBackedTaskManager instanceof FileBackedTaskManager,
                "Класс объекта: InMemoryTaskManager");
    }
}