package service;

import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.service.HistoryManager;
import com.yandex.kanbanboard.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static HistoryManager historyManager;
    private static Task task;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Test","TestDesc");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty(), "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

     @Test
    void getHistory() {
         historyManager.add(task);
         assertTrue(historyManager.getHistory().contains(task), "В истории должна быть задача");
     }

    @Test
    void remove() {
        historyManager.add(task);
        historyManager.remove(task.getId());
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История пустая.");
    }

    @Test
    void CanAddDuplicate() {
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории не должно быть дублирования");
    }
}