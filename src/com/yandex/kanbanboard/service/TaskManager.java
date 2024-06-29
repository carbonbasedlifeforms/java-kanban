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
        return new ArrayList<Task>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<Subtask>(subTasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return (ArrayList<Epic>) epics.values();
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Task getEpicById(int id) {
        return epics.get(id);
    }

    // удалить задачу по Id
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
    }

    // создать задачу, в качестве параметра передается сам объект
    public int createTask(Task task) {
        if (task instanceof Task) {
            final int id = ++taskCounter;
            task.setId(id);
            tasks.put(task.getId(), task);
            return id;
        } else
            System.out.println("Объект не является Task");
            return -1;
    }

    public int createEpic(Epic epic) {
        if (epic instanceof Epic) {
            final int id = ++taskCounter;
            epic.setId(id);
            epics.put(epic.getId(), epic);
            return id;
        } else {
            System.out.println("Объект не является Epic");
            return -1;
        }
    }

    public int createSubtask(Subtask subtask) {
        if (subtask instanceof Subtask) {
            Epic epic = (Epic) getEpicById(subtask.getEpicId());
            if (epic == null) {
                System.out.println("Не найден эпик по подзадаче");
                return -1;
            }
            final int id = ++taskCounter;
            subtask.setId(id);
            subTasks.put(subtask.getId(), subtask);
            epic.addSubTaskToEpic(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            return id;
        } else {
            System.out.println("Объект не является Subtask");
            return -1;
        }
    }

    // получить список сабтасок из эпика
    public ArrayList<Subtask> getSubtasksForEpic(int id) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<Subtask>();
        Epic epic = (Epic) getEpicById(id);
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
            return;
        }
        if (task instanceof Epic || task instanceof Subtask) {
            System.out.println("Объект не является Task");
        } else {
            tasks.replace(task.getId(), task);
        }
    }

    public void updateEpic (Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет");
            return;
        }
        epics.replace(epic.getId(), epic);
    }

    public void updateSubtask (Subtask subtask) {
        if (!subTasks.containsKey(subtask.getId())) {
            System.out.println("Такой подзадачи нет");
            return;
        }
        subTasks.replace(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        updateEpicStatus(epicId);

    }

    private void updateEpicStatus(int epicId) {
        Epic epic = (Epic) getEpicById(epicId);
        ArrayList<Subtask> epicSubtasks = getSubtasksForEpic(epicId);
        if (epicSubtasks == null) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean isAllStatusesDone =  epicSubtasks.stream().allMatch(x -> x.getStatus().equals(TaskStatus.DONE));
        if (isAllStatusesDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        boolean isAllStatusesNew =  epicSubtasks.stream().allMatch(x -> x.getStatus().equals(TaskStatus.NEW));
        if (isAllStatusesNew) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        epic.setStatus(TaskStatus.IN_PROGRESS);
    }
}
