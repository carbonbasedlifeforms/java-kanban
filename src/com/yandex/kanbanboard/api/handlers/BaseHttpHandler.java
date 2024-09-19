package com.yandex.kanbanboard.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanbanboard.api.HttpTaskServer;
import com.yandex.kanbanboard.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {
    Gson gson;
    TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, 0);
        exchange.close();
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String[] stringSplit = exchange.getRequestURI().getPath().split("/");
        String stringId = stringSplit[2];
        if (stringId.matches("[0-9]+")) {
            return Optional.of(Integer.parseInt(stringId));
        }
        return Optional.empty();
    }

    protected <T> Optional<T> parsePostBody(InputStream bodyInputStream, Class<T> classClass) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        return Optional.of(gson.fromJson(body, classClass));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }
}