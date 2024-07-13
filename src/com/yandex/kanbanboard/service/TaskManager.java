package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubTasks();

    ArrayList<Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    ArrayList<Subtask> getSubtasksForEpic(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

}
