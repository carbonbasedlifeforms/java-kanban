package service;

import com.yandex.kanbanboard.service.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager,"Объект менеджера задач должен существовать");
        assertTrue(manager instanceof InMemoryTaskManager, "Класс объекта: InMemoryTaskManager");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager history = Managers.getDefaultHistory();
        assertNotNull(history, "Объект менеджера истории задач должен существовать");
        assertTrue(history instanceof InMemoryHistoryManager, "Класс объекта: InMemoryHistoryManager");
    }
}