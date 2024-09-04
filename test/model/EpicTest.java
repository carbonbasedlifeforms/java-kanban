package model;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("epic name", "epic description");
        subtask = new Subtask("subtask name", "subtask description", epic.getId());
    }

    @Test
    void shouldTwoInstancesOfEpicIsEqualsByEqualsId() {
        Epic epic1 = new Epic(1, "epic1 name", "epic1 description");
        Epic epic2 = new Epic(1, "epic2 name", "epic2 description");
        assertEquals(epic1, epic2, "Объекты Epic с одним ID не равны другу другу");
    }

    @Test
    void getEpicSubtasksIds() {
        assertTrue(epic.getEpicSubtasksIds().isEmpty());
        epic.addSubTaskToEpic(subtask.getId());
        assertFalse(epic.getEpicSubtasksIds().isEmpty());
    }

    @Test
    void addSubTaskToEpic() {
        Subtask newSubtask = new Subtask("New subtask", "New subtask description", epic.getId());
        epic.addSubTaskToEpic(newSubtask.getId());
        assertTrue(epic.getEpicSubtasksIds().contains(newSubtask.getId()));
    }

    @Test
    void deleteSubTask() {
        epic.addSubTaskToEpic(subtask.getId());
        assertTrue(epic.getEpicSubtasksIds().contains(subtask.getId()));
        epic.deleteSubTask(subtask.getId());
        assertFalse(epic.getEpicSubtasksIds().contains(subtask.getId()));
    }

    @Test
    void clearAllEpicSubtasks() {
        epic.addSubTaskToEpic(subtask.getId());
        assertFalse(epic.getEpicSubtasksIds().isEmpty());
        epic.clearAllEpicSubtasks();
        assertTrue(epic.getEpicSubtasksIds().isEmpty());
    }
}