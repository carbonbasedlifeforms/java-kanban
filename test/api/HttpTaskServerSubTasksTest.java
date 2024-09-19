package api;

import com.google.gson.Gson;
import com.yandex.kanbanboard.api.HttpTaskServer;
import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.TaskStatus;
import com.yandex.kanbanboard.service.InMemoryTaskManager;
import com.yandex.kanbanboard.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubTasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    static HttpClient client;
    static URI url;
    HttpRequest request;
    HttpResponse<String> response;
    Epic epic;
    String subtaskJson;


    public HttpTaskServerSubTasksTest() throws IOException {
    }

    @BeforeAll
    public static void beforeAll() {
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/subtasks");
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
        epic = new Epic("Epic 2", "Testing epic 2");
        epic = manager.createEpic(epic);
        Subtask subtask = new Subtask("SubTask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), 5, LocalDateTime.now());
        String subTaskJson = gson.toJson(subtask);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubTask() {
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = manager.getAllSubTasks();
        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("SubTask 1",
                tasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = manager.getAllSubTasks();
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        request = HttpRequest.newBuilder().uri(URI.create(url + "/1")).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        int id = tasksFromManager.getFirst().getId();
        request = HttpRequest.newBuilder().uri(URI.create(url.toString() + "/" + id)).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        tasksFromManager = manager.getAllSubTasks();
        assertEquals(0, tasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Subtask subtaskForUpdate = new Subtask(1, "SubTask 1 updated", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), 5, LocalDateTime.now());
        subtaskJson = gson.toJson(subtaskForUpdate);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        subtaskForUpdate = new Subtask(2, "SubTask 1 updated", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), 5, LocalDateTime.now());
        subtaskJson = gson.toJson(subtaskForUpdate);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("SubTask 1 updated",
                manager.getAllSubTasks().getFirst().getName(), "Некорректное имя подзадачи");
    }
}