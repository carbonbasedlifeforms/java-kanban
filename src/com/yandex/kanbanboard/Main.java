package com.yandex.kanbanboard;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskStatus;
import com.yandex.kanbanboard.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Обычная таска","Сходить в магазин, купить вкусняшки", TaskStatus.NEW);
        taskManager.createTask(task1);
        System.out.println(task1.getStatus());
        task1.setStatus(TaskStatus.DONE);
        task1.setName("Задача стала необычной");
        taskManager.updateTask(task1);
        System.out.println(taskManager.getAllTasks());

        Epic epic = new Epic("Важный эпик 1","Сделать что то важное");
        System.out.println("createEpic Id: " + taskManager.createEpic(epic));

        Epic otherEpic = new Epic("Важный эпик 2","Сделать ещё что нибудь важное");
        System.out.println("otherEpic id: " + taskManager.createEpic(otherEpic));

        Subtask subtask1 = new Subtask("Простая таска 1", "Сделать что то очень простое", epic.getId());
        System.out.println("subtask1: " + taskManager.createSubtask(subtask1));
        Subtask subtask2 = new Subtask("Простая таска 2", "Сделать что то совсем простое", epic.getId());
        System.out.println("subtask2: " + taskManager.createSubtask(subtask2));

        System.out.println(taskManager.getEpicById(2));
        System.out.println("getEpicSubtasksIds:" + epic.getEpicSubtasksIds());


        System.out.println(taskManager.getAllSubTasks());
        System.out.println("task1.equals: " + task1.equals(2));
        System.out.println(taskManager.getSubtasksForEpic(2));

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        System.out.println(epic.getStatus());

        Subtask subtask3 = new Subtask("а вот еще таска", "Сделать еще какую нибудь дичь", epic.getId());
        System.out.println("subtask3: " + taskManager.createSubtask(subtask3));
        System.out.println(epic.getStatus());

        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);

        Subtask subtask4 = new Subtask("и еще таска", "очередное издевательство над эпиком",TaskStatus.NEW, epic.getId());
        System.out.println("subtask4: " + taskManager.createSubtask(subtask4));
        System.out.println(epic.getStatus());

        subtask4.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask4);

        System.out.println(epic.getEpicSubtasksIds());
        System.out.println(taskManager.getAllSubTasks());

        System.out.println(epic.getStatus());


    }
}
