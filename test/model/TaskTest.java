package model;

import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task testTask;

    @BeforeEach
    void beforeEach() {
        testTask = new Task(73, "ideal number task","73 - ideal number");
    }
    @Test
    void shouldTwoInstancesOfTaskIsEqualsByEqualsId() {
        Task task1 = new Task(1, "task name", "task description");
        Task task2 = new Task(1, "task name", "task description");
        assertEquals(task1, task2, "Объекты Task с одним ID не равны другу другу");
    }

    @Test
    void getId() {
        assertEquals(testTask.getId(),73);
    }

    @Test
    void setId() {
        testTask.setId(42);
        assertEquals(testTask.getId(),42);
    }

    @Test
    void getName() {
        assertEquals(testTask.getName(),"ideal number task");
    }

    @Test
    void setName() {
        assertEquals(testTask.getName(),"ideal number task");
        testTask.setName("Changed name");
        assertEquals(testTask.getName(),"Changed name");
    }

    @Test
    void setDescription() {
        assertEquals(testTask.getDescription(),"73 - ideal number");
        testTask.setDescription("Changed description");
        assertEquals(testTask.getDescription(),"Changed description");
    }

    @Test
    void getDescription() {
        assertEquals(testTask.getDescription(),"73 - ideal number");
    }

    @Test
    void getStatus() {
        assertEquals(testTask.getStatus(), TaskStatus.NEW);
    }

    @Test
    void setStatus() {
        assertEquals(testTask.getStatus(), TaskStatus.NEW);
        testTask.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(testTask.getStatus(), TaskStatus.IN_PROGRESS);
    }
}