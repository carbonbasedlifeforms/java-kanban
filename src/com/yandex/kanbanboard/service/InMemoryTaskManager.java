package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.exceptions.NotFoundException;
import com.yandex.kanbanboard.exceptions.ValidationException;
import com.yandex.kanbanboard.model.Epic;
import com.yandex.kanbanboard.model.Subtask;
import com.yandex.kanbanboard.model.Task;
import com.yandex.kanbanboard.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager extends InMemoryHistoryManager implements TaskManager, HistoryManager {
    private int taskCounter = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        sortedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        sortedTasks.removeAll(subTasks.values());
        subTasks.clear();
        //статус эпика переходит в NEW после удаления всех подзадач в методе clearAllEpicSubtasks
        getAllEpics().forEach(Epic::clearAllEpicSubtasks);
    }

    @Override
    public void deleteAllEpics() {
        sortedTasks.removeAll(subTasks.values());
        subTasks.clear();
        sortedTasks.removeAll(epics.values());
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
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.deleteSubTask(id);
        updateEpic(epic);
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        List<Integer> epicSubtasksIds = epics.get(id).getEpicSubtasksIds();
        epicSubtasksIds.forEach(historyManager::remove);
        epicSubtasksIds.forEach(subTasks::remove);
        epics.remove(id);
        historyManager.remove(id);
    }

    // создать задачу, в качестве параметра передается сам объект
    @Override
    public Task createTask(Task task) {
        if (getAllTasks().stream()
                .anyMatch(x -> checkTasksIntersect(x, task))) {
            throw new ValidationException("Задачи пересекаются");
        }
        final int id = ++taskCounter;
        task.setId(id);
        tasks.put(task.getId(), task);
        // не добавляем в TreeSet сортированный по дате старта, если дата старта не задана
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
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
        if (getAllSubTasks().stream()
                .filter(x -> x.getStartTime() != null)
                .anyMatch(x -> checkTasksIntersect(x, subtask)))
            throw new ValidationException("Подзадачи пересекаются");
        Epic epic = epics.get((subtask.getEpicId()));
        if (epic == null) {
            throw new NotFoundException("Не найден эпик по подзадаче");
        }
        final int id = ++taskCounter;
        subtask.setId(id);
        subTasks.put(subtask.getId(), subtask);
        epic.addSubTaskToEpic(subtask.getId());
        updateEpicStatus(epic.getId());
        // не добавляем в TreeSet сортированный по дате старта, если дата старта не задана
        if (subtask.getStartTime() != null) {
            sortedTasks.add(subtask);
            calcAndSetEpicTime(epics.get(subtask.getEpicId()));
        }
        return subtask;
    }

    // получить список сабтасок из эпика
    @Override
    public List<Subtask> getSubtasksForEpic(int id) {
        List<Subtask> subtasksForEpic = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic == null) {
            System.out.println("Не найден эпик по id");
            return Collections.emptyList();
        }
        // тут если реализовывать через стримы будет хуже читаться, поэтому оставил цикл
        for (Integer subtaskId : epic.getEpicSubtasksIds()) {
            subtasksForEpic.add(subTasks.get(subtaskId));
        }
        return subtasksForEpic;
    }

    // обновление задачи, в качестве параметра передается сам объект,
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Такой задачи нет");
        } else {
            tasks.replace(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new NotFoundException("Такого эпика нет");
        } else {
            epics.replace(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subTasks.containsKey(subtask.getId())) {
            throw new NotFoundException("Такой подзадачи нет");
        } else {
            subTasks.replace(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            calcAndSetEpicTime(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void fillTaskManagerMaps(Task task) {
        switch (task.getTaskType()) {
            case TASK -> tasks.put(task.getId(), task);
            case EPIC -> epics.put(task.getId(), (Epic) task);
            case SUBTASK -> subTasks.put(task.getId(), (Subtask) task);
        }
        taskCounter = Math.max(taskCounter, task.getId());
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            throw new NotFoundException("Не найден эпик по Id");
        List<Subtask> epicSubtasks = getSubtasksForEpic(epicId);
        if (epicSubtasks == null || epicSubtasks.stream()
                .allMatch(x -> x.getStatus().equals(TaskStatus.NEW))) {
            epic.setStatus(TaskStatus.NEW);
        } else if (epicSubtasks.stream()
                .allMatch(x -> x.getStatus().equals(TaskStatus.DONE))) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    protected void calcAndSetEpicTime(Epic epic) {
        Duration epicDuration;
        LocalDateTime epicEndTime = LocalDateTime.MIN;
        LocalDateTime epicStartTime = LocalDateTime.MAX;
        List<Subtask> subtasks = getSubtasksForEpic(epic.getId());
        if (subtasks.isEmpty())
            return;
        epicDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(epicDuration);
        epicStartTime = subtasks.stream()
                .min(Comparator.comparing(Subtask::getStartTime))
                .map(Subtask::getStartTime)
                .orElse(epicStartTime);
        epicEndTime = subtasks.stream()
                .max(Comparator.comparing(Subtask::getEndTime))
                .map(Subtask::getEndTime)
                .orElse(epicEndTime);
        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);
    }

    @Override
    public boolean checkTasksIntersect(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null)
            return false;
        return task1.getStartTime()
                .isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime());
    }

    @Override
    public void addToSortedTasks(Task task) {
        if (task.getStartTime() != null)
            sortedTasks.add(task);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream()
                .toList();
    }
}
