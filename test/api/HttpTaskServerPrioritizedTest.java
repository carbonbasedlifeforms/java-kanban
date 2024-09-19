package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yandex.kanbanboard.api.HttpTaskServer;
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
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerPrioritizedTest {
    private static final DateTimeFormatter FORMAT_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    static HttpClient client;
    static URI url;
    HttpResponse<String> response;
    HttpRequest request;

    Task task;
    Task anotherTask;

    public HttpTaskServerPrioritizedTest() throws IOException {
    }

    @BeforeAll
    public static void beforeAll() {
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/prioritized");
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
        task = new Task("Test add new Task", "Test task description", TaskStatus.NEW, 1,
                LocalDateTime.parse("2024-09-03 12:00:00", FORMAT_PATTERN));
        anotherTask = new Task("Test add new Task", "Test task description", TaskStatus.NEW, 1,
                LocalDateTime.parse("2024-09-03 11:00:00", FORMAT_PATTERN));
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
        manager.createTask(task);
        manager.createTask(anotherTask);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray(), "неверный формат");
        JsonArray array = element.getAsJsonArray();
        String timeString1 = array.get(0).getAsJsonObject().get("startTime").getAsString();
        String timeString2 = array.get(1).getAsJsonObject().get("startTime").getAsString();
        assertTrue(LocalDateTime.parse(timeString1, FORMAT_PATTERN)
                .isBefore(LocalDateTime.parse(timeString2, FORMAT_PATTERN)), "не в том порядке");
    }
}
