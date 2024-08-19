package com.yandex.kanbanboard;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.service.FileBackedTaskManager;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(Paths.get("bcp.csv").toFile());
        System.out.println(fileManager.getAllTasks());
        System.out.println(fileManager.getAllEpics());
        System.out.println(fileManager.getAllSubTasks());
        Task task23 = new Task("Task 23", "Description 23");
        fileManager.createTask(task23);
        Task task42 = new Task("Task 42", "Description 42");
        fileManager.createTask(task42);
        Epic epic73 = new Epic("Epic 73", "Description 73");
        fileManager.createEpic(epic73);
        Epic epic404 = new Epic("Epic 404", "Description 404");
        fileManager.createEpic(epic404);
        Subtask subtask111 = new Subtask("Subtask 111", "Description 111", epic73.getId());
        Subtask subtask222 = new Subtask("Subtask 222", "Description 222", epic73.getId());
        Subtask subtask333 = new Subtask("Subtask 333", "Description 333", epic73.getId());
        fileManager.createSubtask(subtask111);
        fileManager.createSubtask(subtask222);
        fileManager.createSubtask(subtask333);
    }
}
