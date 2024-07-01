package com.yandex.kanbanboard.service;
import com.yandex.kanbanboard.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int taskCounter = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : getAllEpics()) {
            //статус эпика переходит в NEW после удаления всех подзадач в методе clearAllEpicSubtasks
            epic.clearAllEpicSubtasks();
        }
    }

    public void deleteAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // удалить задачу по Id
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubTaskById(int id) {
        Epic epic = getEpicById(getSubTaskById(id).getEpicId());
        epic.deleteSubTask(id);
        updateEpic(epic);
        subTasks.remove(id);
    }

    public void deleteEpicById(int id) {
        getEpicById(id).getEpicSubtasksIds().forEach(subTasks::remove);
        epics.remove(id);
    }

    // создать задачу, в качестве параметра передается сам объект
    public Task createTask(Task task) {
            final int id = ++taskCounter;
            task.setId(id);
            tasks.put(task.getId(), task);
            return task;
    }

    public Epic createEpic(Epic epic) {
            final int id = ++taskCounter;
            epic.setId(id);
            epics.put(epic.getId(), epic);
            return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
            Epic epic = getEpicById(subtask.getEpicId());
            if (epic == null) {
                System.out.println("Не найден эпик по подзадаче");
                return null;
            }
            final int id = ++taskCounter;
            subtask.setId(id);
            subTasks.put(subtask.getId(), subtask);
            epic.addSubTaskToEpic(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            return subtask;
    }

    // получить список сабтасок из эпика
    public ArrayList<Subtask> getSubtasksForEpic(int id) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic epic = getEpicById(id);
        if (epic == null) {
            System.out.println("Не найден эпик по id");
            return null;
        }
        for (Integer subtaskId : epic.getEpicSubtasksIds()) {
            subtasksForEpic.add(subTasks.get(subtaskId));
        }
        return subtasksForEpic;
    }

    // обновление задачи, в качестве параметра передается сам объект,
    public void updateTask (Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет");
        } else {
            tasks.replace(task.getId(), task);
        }
    }

    public void updateEpic (Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет");
        } else {
            epics.replace(epic.getId(), epic);
        }
    }

    public void updateSubtask (Subtask subtask) {
        if (!subTasks.containsKey(subtask.getId())) {
            System.out.println("Такой подзадачи нет");
        } else {
            subTasks.replace(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }

    }

    private void updateEpicStatus(int epicId) {
        Epic epic = getEpicById(epicId);
        ArrayList<Subtask> epicSubtasks = getSubtasksForEpic(epicId);
        if (epicSubtasks == null || epicSubtasks.stream().allMatch(x -> x.getStatus().equals(TaskStatus.NEW))) {
            epic.setStatus(TaskStatus.NEW);
        } else if (epicSubtasks.stream().allMatch(x -> x.getStatus().equals(TaskStatus.DONE))){
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
