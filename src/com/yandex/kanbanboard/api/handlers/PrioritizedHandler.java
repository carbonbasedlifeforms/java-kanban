package com.yandex.kanbanboard.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.exceptions.SendResponseException;
import com.yandex.kanbanboard.service.TaskManager;

import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/prioritized";

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                if (Pattern.matches("^" + PATH_ENTITY + "$", exchange.getRequestURI().getPath())) {
                    sendText(exchange, 200, gson.toJson(taskManager.getPrioritizedTasks()));
                }
            } else {
                sendText(exchange, 405, "Method not allowed");
            }
        } catch (SendResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            sendText(exchange, 500, e.getMessage());
        }
    }
}
