package com.yandex.kanbanboard.api.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.exceptions.NotFoundException;
import com.yandex.kanbanboard.exceptions.ValidationException;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.TaskTypes;
import com.yandex.kanbanboard.service.TaskManager;

import java.util.Optional;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/subtasks";

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "$", path))
                        sendText(exchange, 200, gson.toJson(taskManager.getAllSubTasks()));
                    else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> subTaskId = getIdFromPath(exchange);
                        if (subTaskId.isEmpty() || taskManager.getSubTaskById(subTaskId.get()) == null) {
                            sendText(exchange, 404, "task not found");
                            return;
                        }
                        Subtask subtask = taskManager.getSubTaskById(subTaskId.get());
                        sendText(exchange, 200, gson.toJson(subtask));
                    } else {
                        sendText(exchange, 404, "task not found");
                    }
                }
                case "DELETE" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> subTaskId = getIdFromPath(exchange);
                        if (subTaskId.isEmpty() || taskManager.getSubTaskById(subTaskId.get()) == null) {
                            sendText(exchange, 404, "task not found");
                            return;
                        }
                        taskManager.deleteSubTaskById(subTaskId.get());
                        sendText(exchange, 200, "task " + subTaskId.get() + " deleted successfully");
                    } else {
                        sendText(exchange, 404, "task not found");
                    }
                }
                case "POST" -> {
                    Subtask subtask = parsePostBody(exchange.getRequestBody(), Subtask.class)
                            .orElseThrow(() -> new JsonSyntaxException("subtask body is not correct"));
                    if (!TaskTypes.SUBTASK.equals(subtask.getTaskType())) {
                        sendText(exchange, 400, "bad request");
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
                    sendText(exchange, 201, "subtask created successfully");
                }
                default -> sendText(exchange, 405, "method not allowed");
            }
        } catch (ValidationException e) {
            sendText(exchange, 406, e.getMessage());
        } catch (JsonSyntaxException e) {
            sendText(exchange, 400, e.getMessage());
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, 500, e.getMessage());
        }
    }
}
