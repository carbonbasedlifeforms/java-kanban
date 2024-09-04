package com.yandex.kanbanboard;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskStatus;
import com.yandex.kanbanboard.service.FileBackedTaskManager;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(Paths.get("bcp.csv").toFile());
        System.out.println(fileManager.getAllTasks());
        System.out.println(fileManager.getAllEpics());
        System.out.println(fileManager.getAllSubTasks());

        Task task23 = new Task("Task 23", "Description 23",
                TaskStatus.NEW, 100, LocalDateTime.parse("2024-01-01 12:00:00", dateFormat));
        fileManager.createTask(task23);
        Task task42 = new Task("Task 42", "Description 42", TaskStatus.NEW,
                200, LocalDateTime.parse("2024-02-01 11:00:00", dateFormat));
        fileManager.createTask(task42);
        Epic epic73 = new Epic("Epic 73", "Description 73");
        fileManager.createEpic(epic73);
        Epic epic404 = new Epic("Epic 404", "Description 404");
        fileManager.createEpic(epic404);
        Subtask subtask111 = new Subtask("Subtask 111", "Description 111", TaskStatus.NEW,
                epic73.getId(), 200, LocalDateTime.parse("2024-03-01 14:30:00", dateFormat));
        Subtask subtask222 = new Subtask("Subtask 222", "Description 222", TaskStatus.NEW,
                epic73.getId(), 300, LocalDateTime.parse("2024-03-10 19:00:00", dateFormat));
        fileManager.createSubtask(subtask111);
        fileManager.createSubtask(subtask222);

        System.out.println("sorted list:");
        fileManager.getPrioritizedTasks()
                .forEach(x -> System.out.println(x.getId() + " : " + x.getTaskType()
                        + " : " + x.getDuration().toMinutes() + " : " + x.getStartTime() + " : " + x.getEndTime()));

    }
}
