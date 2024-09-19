package api;

import com.google.gson.Gson;
import com.yandex.kanbanboard.api.HttpTaskServer;
import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.service.InMemoryTaskManager;
import com.yandex.kanbanboard.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerEpicsTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    static HttpClient client;
    static URI url;

    Epic epic;
    String epicJson;
    HttpRequest request;
    HttpResponse<String> response;

    public HttpTaskServerEpicsTest() throws IOException {
    }

    @BeforeAll
    public static void beforeAll() {
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics");
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
        epic = new Epic("Epic 1", "Testing epic 1");
        epicJson = gson.toJson(epic);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() {
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException, URISyntaxException {
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        URI uriId = new URI(url + "/1");
        request = HttpRequest.newBuilder()
                .uri(uriId)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        epicsFromManager = manager.getAllEpics();
        assertEquals(0, epicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(epicsFromManager.size(),
                gson.fromJson(response.body(), List.class).size(), "Некорректное количество эпиков");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic forUpdate = new Epic(1, "Epic 1 updated", "Testing epic 1");
        epicJson = gson.toJson(forUpdate);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Epic 1 updated",
                manager.getAllEpics().getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetSubtasksForEpic() throws IOException, InterruptedException {
        request = HttpRequest.newBuilder().uri(URI.create(url + "/1/subtasks")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.fromJson(response.body(), List.class).size(),
                manager.getSubtasksForEpic(1).size(), "Некорректное количество подзадач");
    }
}