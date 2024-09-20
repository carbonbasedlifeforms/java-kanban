package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yandex.kanbanboard.api.HttpTaskServer;
import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerHistoryTest {
    TaskManager manager;
    HttpTaskServer taskServer;

    static HttpClient client;
    static URI url;
    HttpResponse<String> response;
    HttpRequest request;

    Task task;
    Epic epic;
    Subtask subtask;

    public HttpTaskServerHistoryTest() {
    }

    @BeforeAll
    public static void beforeAll() {
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/history");
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
        task = new Task("task 1", "Testing task 1",
                TaskStatus.NEW, 5, LocalDateTime.now());
        manager.createTask(task);
        epic = new Epic("Test 1", "Testing epic 1");
        manager.createEpic(epic);
        subtask = new Subtask("SubTask 1", "Testing subtask 1",
                TaskStatus.NEW, epic.getId(), 5, LocalDateTime.now());
        manager.createSubtask(subtask);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(url.resolve(url))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body(), "не пустая история");
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subtask.getId());
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray(), "неверный формат");
        JsonArray array = element.getAsJsonArray();
        assertEquals(task.getId(),
                array.get(0).getAsJsonObject().get("id").getAsInt(), "в истории нет задачи");
        assertEquals(epic.getId(),
                array.get(1).getAsJsonObject().get("id").getAsInt(), "в истории нет эпика");
        assertEquals(subtask.getId(),
                array.get(2).getAsJsonObject().get("id").getAsInt(), "в истории нет подзадачи");
    }
}
