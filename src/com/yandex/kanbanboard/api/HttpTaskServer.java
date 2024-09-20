package com.yandex.kanbanboard.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.yandex.kanbanboard.api.adapters.DurationAdapter;
import com.yandex.kanbanboard.api.adapters.LocalDateTimeAdapter;
import com.yandex.kanbanboard.api.handlers.*;
import com.yandex.kanbanboard.service.Managers;
import com.yandex.kanbanboard.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final String IP = "localhost";
    private static final int PORT = 8080;
    private static final int BACKLOG = 0;
    private static final int DELAY = 1;
    protected final HttpServer httpServer;
    public final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(IP, PORT), BACKLOG);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(DELAY);
    }

}