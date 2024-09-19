package com.yandex.kanbanboard.api.handlers;


import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.exceptions.NotFoundException;
import com.yandex.kanbanboard.exceptions.ValidationException;
import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.TaskTypes;
import com.yandex.kanbanboard.service.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/epics";

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "$", path))
                        sendText(exchange, gson.toJson(taskManager.getAllEpics()));
                    else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> epicId = getIdFromPath(exchange);
                        if (epicId.isEmpty() || taskManager.getEpicById(epicId.get()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        Epic epic = taskManager.getEpicById(epicId.get());
                        sendText(exchange, gson.toJson(epic));
                    } else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+/subtasks$", path)) {
                        Optional<Integer> epicId = getIdFromPath(exchange);
                        if (epicId.isEmpty() || taskManager.getEpicById(epicId.get()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        sendText(exchange, gson.toJson(taskManager.getSubtasksForEpic(epicId.get())));
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "DELETE" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> epicId = getIdFromPath(exchange);
                        if (epicId.isEmpty() || taskManager.getEpicById(epicId.get()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        taskManager.deleteEpicById(epicId.get());
                        sendText(exchange, "epic " + epicId.get() + " deleted successfully");
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "POST" -> {
                    Epic epic;
                    Optional<Epic> epicOptional = parsePostBody(exchange.getRequestBody(), Epic.class);
                    if (epicOptional.isEmpty()) {
                        sendBadRequest(exchange);
                        return;
                    } else {
                        epic = epicOptional.get();
                    }
                    if (!TaskTypes.EPIC.equals(epic.getTaskType())) {
                        sendBadRequest(exchange);
                        return;
                    }
                    if (epic.getEpicSubtasksIds() == null || !epic.getEpicSubtasksIds().isEmpty()) {
                        sendBadRequest(exchange);
                        return;
                    }
                    if (epic.getId() != 0) {
                        taskManager.updateEpic(epic);
                    } else {
                        taskManager.createEpic(epic);
                    }
                    sendCreated(exchange);
                }
                default -> sendNotAllowed(exchange);
            }
        } catch (ValidationException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
