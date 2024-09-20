package com.yandex.kanbanboard.api.handlers;


import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.exceptions.NotFoundException;
import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.TaskTypes;
import com.yandex.kanbanboard.service.TaskManager;

import java.util.Optional;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/epics";

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "$", path))
                        sendText(exchange, 200, gson.toJson(taskManager.getAllEpics()));
                    else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> epicId = getIdFromPath(exchange);
                        if (epicId.isEmpty() || taskManager.getEpicById(epicId.get()) == null) {
                            sendText(exchange, 404, "epic not found");
                            return;
                        }
                        Epic epic = taskManager.getEpicById(epicId.get());
                        sendText(exchange, 200, gson.toJson(epic));
                    } else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+/subtasks$", path)) {
                        Optional<Integer> epicId = getIdFromPath(exchange);
                        if (epicId.isEmpty() || taskManager.getEpicById(epicId.get()) == null) {
                            sendText(exchange, 404, "epic not found");
                            return;
                        }
                        sendText(exchange, 200, gson.toJson(taskManager.getSubtasksForEpic(epicId.get())));
                    } else {
                        sendText(exchange, 404, "epic not found");
                    }
                }
                case "DELETE" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> epicId = getIdFromPath(exchange);
                        if (epicId.isEmpty() || taskManager.getEpicById(epicId.get()) == null) {
                            sendText(exchange, 404, "epic not found");
                            return;
                        }
                        taskManager.deleteEpicById(epicId.get());
                        sendText(exchange, 200, "epic " + epicId.get() + " deleted successfully");
                    } else {
                        sendText(exchange, 404, "epic not found");
                    }
                }
                case "POST" -> {
                    Epic epic = parsePostBody(exchange.getRequestBody(), Epic.class)
                            .orElseThrow(() -> new JsonSyntaxException("epic body is not correct"));
                    if (!TaskTypes.EPIC.equals(epic.getTaskType())) {
                        sendText(exchange, 400, "epic type is not EPIC");
                        return;
                    }
                    if (epic.getEpicSubtasksIds() == null || !epic.getEpicSubtasksIds().isEmpty()) {
                        sendText(exchange, 400, "bad request");
                        return;
                    }
                    if (epic.getId() != 0) {
                        taskManager.updateEpic(epic);
                    } else {
                        taskManager.createEpic(epic);
                    }
                    sendText(exchange, 201, "epic created successfully");
                }
                default -> sendText(exchange, 400, "bad request");
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, 400, e.getMessage());
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, 500, e.getMessage());
        }
    }
}
