package com.yandex.kanbanboard.api.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.exceptions.NotFoundException;
import com.yandex.kanbanboard.exceptions.ValidationException;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.TaskTypes;
import com.yandex.kanbanboard.service.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/subtasks";

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "$", path))
                        sendText(exchange, gson.toJson(taskManager.getAllSubTasks()));
                    else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> subTaskId = getIdFromPath(exchange);
                        if (subTaskId.isEmpty() || taskManager.getSubTaskById(subTaskId.get()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        Subtask subtask = taskManager.getSubTaskById(subTaskId.get());
                        sendText(exchange, gson.toJson(subtask));
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "DELETE" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> subTaskId = getIdFromPath(exchange);
                        if (subTaskId.isEmpty() || taskManager.getSubTaskById(subTaskId.get()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        taskManager.deleteSubTaskById(subTaskId.get());
                        sendText(exchange, "task " + subTaskId.get() + " deleted successfully");
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "POST" -> {
                    Subtask subtask;
                    Optional<Subtask> subtaskOptional = parsePostBody(exchange.getRequestBody(), Subtask.class);
                    if (subtaskOptional.isEmpty()) {
                        sendBadRequest(exchange);
                        return;
                    }
                    subtask = subtaskOptional.get();
                    if (!TaskTypes.SUBTASK.equals(subtask.getTaskType())) {
                        sendBadRequest(exchange);
                        return;
                    }
                    if (subtask.getEndTime() == null) {
                        subtask.setEndTime(subtask.getStartTime().plus(subtask.getDuration()));
                    }
                    if (subtask.getId() != 0) {
                        taskManager.updateSubtask(subtask);
                    } else {
                        taskManager.createSubtask(subtask);
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
