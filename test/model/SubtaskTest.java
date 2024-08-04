package model;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void shouldTwoInstancesOfSubtaskIsEqualsByEqualsId() {
        Epic epic = new Epic(1,"Epic1","Epic description1");
        Subtask subtask1 = new Subtask(2, "Subtask1", "Subtask description1", epic.getId());
        Subtask subtask2 = new Subtask(2, "Subtask2", "Subtask description2", epic.getId());
        assertEquals(subtask1, subtask2, "Объекты Epic с одним ID не равны другу другу");
    }

    @Test
    void getEpicId() {
        Epic epic = new Epic(1,"Epic1","Epic description");
        Subtask subtask = new Subtask(2, "Subtask", "Subtask description", epic.getId());
        assertEquals(subtask.getEpicId(),epic.getId());
    }
}