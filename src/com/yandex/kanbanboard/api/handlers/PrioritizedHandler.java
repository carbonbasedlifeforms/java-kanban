package com.yandex.kanbanboard.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/prioritized";

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                if (Pattern.matches("^" + PATH_ENTITY + "$", exchange.getRequestURI().getPath())) {
                    sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
                }
            } else {
                sendNotAllowed(exchange);
            }
        } catch (IOException e) {
            sendInternalError(exchange);
        }
    }
}
