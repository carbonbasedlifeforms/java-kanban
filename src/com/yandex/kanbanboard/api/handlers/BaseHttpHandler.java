package com.yandex.kanbanboard.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.kanbanboard.api.HttpTaskServer;
import com.yandex.kanbanboard.exceptions.SendResponseException;
import com.yandex.kanbanboard.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;
    protected final TaskManager taskManager;


    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    protected void sendText(HttpExchange exchange, int code, String text) {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try {
            exchange.sendResponseHeaders(code, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new SendResponseException(e.getMessage());
        }
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
        return Optional.ofNullable(gson.fromJson(body, classClass));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }
}