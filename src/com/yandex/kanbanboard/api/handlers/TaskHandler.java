package com.yandex.kanbanboard.api.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.kanbanboard.exceptions.NotFoundException;
import com.yandex.kanbanboard.exceptions.SendResponseException;
import com.yandex.kanbanboard.exceptions.ValidationException;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskTypes;
import com.yandex.kanbanboard.service.TaskManager;

import java.util.Optional;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    private static final String PATH_ENTITY = "/tasks";

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "$", path))
                        sendText(exchange, 200, gson.toJson(taskManager.getAllTasks()));
                    else if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> taskId = getIdFromPath(exchange);
                        if (taskId.isEmpty() || taskManager.getTaskById(taskId.get()) == null) {
                            sendText(exchange, 404, "task not found");
                            return;
                        }
                        Task task = taskManager.getTaskById(taskId.get());
                        sendText(exchange, 200, gson.toJson(task));
                    } else {
                        sendText(exchange, 404, "task not found");
                    }
                }
                case "DELETE" -> {
                    if (Pattern.matches("^" + PATH_ENTITY + "/\\d+$", path)) {
                        Optional<Integer> taskId = getIdFromPath(exchange);
                        if (taskId.isEmpty() || taskManager.getTaskById(taskId.get()) == null) {
                            sendText(exchange, 404, "task not found");
                            return;
                        }
                        taskManager.deleteTaskById(taskId.get());
                        sendText(exchange, 200, "task " + taskId.get() + " deleted successfully");
                    } else {
                        sendText(exchange, 404, "task not found");
                    }
                }
                case "POST" -> {
                    Task task = parsePostBody(exchange.getRequestBody(), Task.class)
                            .orElseThrow(() -> new JsonSyntaxException("task body is not correct"));
                    if (!TaskTypes.TASK.equals(task.getTaskType())) {
                        sendText(exchange, 400, "task type is not correct");
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
                    sendText(exchange, 201, "task created successfully");
                }
                default -> sendText(exchange, 405, "method not allowed");
            }
        } catch (ValidationException e) {
            sendText(exchange, 406, e.getMessage());
        } catch (JsonSyntaxException e) {
            sendText(exchange, 400, e.getMessage());
        } catch (NotFoundException e) {
            sendText(exchange, 404, e.getMessage());
        } catch (SendResponseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            sendText(exchange, 500, e.getMessage());
        }
    }
}
