package com.yandex.kanbanboard.api.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.exceptions.NotFoundException;
import com.yandex.kanbanboard.exceptions.ValidationException;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskTypes;
import com.yandex.kanbanboard.service.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/tasks";

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "$", path))
                        sendText(exchange, gson.toJson(taskManager.getAllTasks()));
                    else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> taskId = getIdFromPath(exchange);
                        if (taskId.isEmpty() || taskManager.getTaskById(taskId.get()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        Task task = taskManager.getTaskById(taskId.get());
                        sendText(exchange, gson.toJson(task));
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "DELETE" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> taskId = getIdFromPath(exchange);
                        if (taskId.isEmpty() || taskManager.getTaskById(taskId.get()) == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        taskManager.deleteTaskById(taskId.get());
                        sendText(exchange, "task " + taskId.get() + " deleted successfully");
                    } else {
                        sendNotFound(exchange);
                    }
                }
                case "POST" -> {
                    Task task;
                    Optional<Task> taskOptional = parsePostBody(exchange.getRequestBody(), Task.class);
                    if (taskOptional.isEmpty()) {
                        sendBadRequest(exchange);
                        return;
                    }
                    task = taskOptional.get();
                    if (!TaskTypes.TASK.equals(task.getTaskType())) {
                        sendBadRequest(exchange);
                        return;
                    }
                    if (task.getEndTime() == null) {
                        task.setEndTime(task.getStartTime().plus(task.getDuration()));
                    }
                    if (task.getId() != 0) {
                        taskManager.updateTask(task);
                    } else {
                        taskManager.createTask(task);
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
