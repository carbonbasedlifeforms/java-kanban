package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Subtask> getAllSubTasks();

    List<Epic> getAllEpics();

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

    List<Subtask> getSubtasksForEpic(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getHistory();

    void fillTaskManagerMaps(Task task);

    void addToSortedTasks(Task task);
    List<Task> getPrioritizedTasks();

    boolean checkTasksIntersect(Task task1, Task task2);
}
