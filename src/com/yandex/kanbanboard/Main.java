package com.yandex.kanbanboard;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.service.Managers;
import com.yandex.kanbanboard.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task = new Task("Task 1", "Description 1");
        manager.createTask(task);
        Task task2 = new Task("Task 2", "Description 2");
        manager.createTask(task2);
        Epic epic = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic);
        Epic epic2 = new Epic("Epic 2", "Description 2");
        manager.createEpic(epic2);
        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3", epic.getId());
        System.out.println("-".repeat(10));
        System.out.println(epic.getId());
        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        System.out.println(manager.getHistory());
        manager.getTaskById(task.getId());
        System.out.println(manager.getHistory());
        manager.getTaskById(task2.getId());
        System.out.println(manager.getHistory());
        manager.getTaskById(task.getId());
        System.out.println(manager.getHistory());
        manager.getTaskById(task2.getId());
        System.out.println(manager.getHistory());
        manager.getEpicById(epic.getId());
        System.out.println(manager.getHistory());
        manager.getSubTaskById(subtask.getId());
        System.out.println(manager.getHistory());
        manager.getSubTaskById(subtask2.getId());
        System.out.println(manager.getHistory());
        manager.getSubTaskById(subtask.getId());
        System.out.println(manager.getHistory());
        System.out.println("-".repeat(10));

        manager.deleteTaskById(task.getId());
        System.out.println(manager.getHistory());
        manager.deleteSubTaskById(subtask.getId());
        System.out.println(manager.getHistory());
        manager.deleteEpicById(epic.getId());
        System.out.println(manager.getHistory());
    }
}
