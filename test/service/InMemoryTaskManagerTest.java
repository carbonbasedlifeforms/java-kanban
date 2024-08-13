package service;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskStatus;
import com.yandex.kanbanboard.service.InMemoryTaskManager;
import com.yandex.kanbanboard.service.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;
    private Task task;
    private Subtask subTask;
    private Epic epic;

    @BeforeAll
    static void beforeAll() {
        taskManager = new InMemoryTaskManager();
    }

    @BeforeEach
    void setUp() {
        task = new Task("Test add new Task", "Test task description");
        epic = new Epic("Test add new Epic", "Test epic description");

        taskManager.createTask(task);
        taskManager.createEpic(epic);

        subTask = new Subtask("Test add new Subtask", "Test subtask description", epic.getId());
        taskManager.createSubtask(subTask);
    }

    @Test
    void getAllTasks() {
        assertFalse(taskManager.getAllTasks().isEmpty(), "Список задач не должен быть пустым");
    }

    @Test
    void getAllSubTasks() {
        assertFalse(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не должен быть пустым");
    }

    @Test
    void getAllEpics() {
        assertFalse(taskManager.getAllEpics().isEmpty(), "Список эпиков не должен быть пустым");
    }

    @Test
    void deleteAllTasks() {
        assertFalse(taskManager.getAllTasks().isEmpty(), "Список задач не должен быть пустым");
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void deleteAllSubTasks() {
        epic.setStatus(TaskStatus.DONE);
        assertFalse(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не должен быть пустым");
        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач должен быть пустым");
        assertTrue(taskManager.getAllEpics().stream().allMatch(x -> x.getStatus().equals(TaskStatus.NEW)));
    }

    @Test
    void deleteAllEpics() {
        assertFalse(taskManager.getAllEpics().isEmpty(), "Список эпиков не должен быть пустым");
        assertFalse(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не должен быть пустым");
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void getTaskById() {
        assertNotNull(taskManager.getTaskById(task.getId()), "Задача должна возвращаться по ID");
    }

    @Test
    void getSubTaskById() {
        assertNotNull(taskManager.getSubTaskById(subTask.getId()), "Подзадача должна возвращаться по ID");
    }

    @Test
    void getEpicById() {
        assertNotNull(taskManager.getEpicById(epic.getId()), "Эпик должен возвращаться по ID");
    }

    @Test
    void deleteTaskById() {
        assertNotNull(taskManager.getTaskById(task.getId()), "Задача должна возвращаться по ID");
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()), "Задачи не должно быть в списке задач");
    }

    @Test
    void deleteSubTaskById() {
        int subtaskId = subTask.getId();
        assertNotNull(taskManager.getSubTaskById(subtaskId),
                "Подзадача эпика должна возвращаться по ID");
        int subtaskEpicId = subTask.getEpicId();
        assertTrue(taskManager.getEpicById(subtaskEpicId).getEpicSubtasksIds().contains(subtaskId),
                "В Эпике должна быть подзадача");
        taskManager.deleteSubTaskById(subtaskId);
        assertNull(taskManager.getSubTaskById(subtaskId), "Подзадачи не должно быть в списке подзадач");
        assertFalse(taskManager.getEpicById(subtaskEpicId).getEpicSubtasksIds().contains(subtaskId),
                "В Эпике не должно быть удаленной подзадачи");
    }

    @Test
    void deleteEpicById() {
        taskManager.deleteEpicById(epic.getId());
        assertTrue(taskManager.getAllSubTasks().stream().noneMatch(x -> x.getEpicId() == epic.getId()),
                "В списке подзадач не должно быть подзадач удаленного эпика");
    }

    @Test
    void createTask() {
        Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void createSubtask() {
        Subtask savedSubtask = taskManager.getSubTaskById(subTask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subTask, savedSubtask, "Подзадачи не совпадают");
    }

    @Test
    void getSubtasksForEpic() {
        assertFalse(epic.getEpicSubtasksIds().isEmpty(), "Список id подзадач пустой для Эпика");
    }

    @Test
    void updateTask() {
        task.setName("New task name");
        task.setDescription("New task description");
        task.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(updatedTask.getName(), "New task name", "Имя не совпадает");
        assertEquals(updatedTask.getDescription(), "New task description", "Описание не совпадает");
        assertEquals(updatedTask.getStatus(), TaskStatus.DONE, "Статус не совпадает");
    }

    @Test
    void updateEpic() {
        epic.setName("New epic name");
        epic.setDescription("New epic description");
        epic.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(updatedEpic.getName(), "New epic name");
        assertEquals(updatedEpic.getDescription(), "New epic description");
        assertEquals(updatedEpic.getStatus(), TaskStatus.DONE);
    }

    @Test
    void updateSubtask() {
        subTask.setName("New subtask name");
        subTask.setDescription("New subtask description");
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subTask);
        Subtask updatedSubtask = taskManager.getSubTaskById(subTask.getId());
        assertEquals(updatedSubtask.getName(), "New subtask name");
        assertEquals(updatedSubtask.getDescription(), "New subtask description");
        assertEquals(updatedSubtask.getStatus(), TaskStatus.DONE);
    }
}