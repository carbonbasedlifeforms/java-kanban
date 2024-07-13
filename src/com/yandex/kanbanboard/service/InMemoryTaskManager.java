package com.yandex.kanbanboard.service;
import com.yandex.kanbanboard.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager extends InMemoryHistoryManager implements TaskManager, HistoryManager {
    private static int taskCounter = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : getAllEpics()) {
            //статус эпика переходит в NEW после удаления всех подзадач в методе clearAllEpicSubtasks
            epic.clearAllEpicSubtasks();
        }
    }

    @Override
    public void deleteAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    // удалить задачу по Id
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
//        Epic epic = getEpicById(getSubTaskById(id).getEpicId());
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.deleteSubTask(id);
        updateEpic(epic);
        subTasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        epics.get(id).getEpicSubtasksIds().forEach(subTasks::remove);
        epics.remove(id);
    }

    // создать задачу, в качестве параметра передается сам объект
    @Override
    public Task createTask(Task task) {
        final int id = ++taskCounter;
        task.setId(id);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        final int id = ++taskCounter;
        epic.setId(id);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get((subtask.getEpicId()));
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
    @Override
    public ArrayList<Subtask> getSubtasksForEpic(int id) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic epic = epics.get(id);
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
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет");
        } else {
            tasks.replace(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет");
        } else {
            epics.replace(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subTasks.containsKey(subtask.getId())) {
            System.out.println("Такой подзадачи нет");
        } else {
            subTasks.replace(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> epicSubtasks = getSubtasksForEpic(epicId);
        if (epicSubtasks == null || epicSubtasks.stream().allMatch(x -> x.getStatus().equals(TaskStatus.NEW))) {
            epic.setStatus(TaskStatus.NEW);
        } else if (epicSubtasks.stream().allMatch(x -> x.getStatus().equals(TaskStatus.DONE))) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
