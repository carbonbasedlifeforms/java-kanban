package com.yandex.kanbanboard.service;

import com.yandex.kanbanboard.exceptions.ManagerSaveException;
import com.yandex.kanbanboard.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    public static final String DELIMITER_CSV = ",";
    public static final String FILE_HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(FILE_HEADER);
            bufferedWriter.newLine();
            for (Task task : getAllTasks()) {
                bufferedWriter.write(toString(task));
                bufferedWriter.newLine();
            }
            for (Epic epic : getAllEpics()) {
                bufferedWriter.write(toString(epic));
                bufferedWriter.newLine();
            }
            for (Subtask subtask : getAllSubTasks()) {
                bufferedWriter.write(toString(subtask));
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error when saving to file" + file.getName(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
        fileManager.loadFromFile();
        return fileManager;
    }

    private void loadFromFile() throws IOException {
        try (BufferedReader buffer = new BufferedReader(new FileReader(this.file, StandardCharsets.UTF_8))) {
            buffer.readLine();
            while (buffer.ready()) {
                Task task = fromString(buffer.readLine());
                fillTaskManagerMaps(task);
            }
            getAllSubTasks().forEach(x -> restoreSubtasksListForEpic(x));
        } catch (FileNotFoundException e) {
            System.out.println();
            file = Files.createFile(Path.of(file.getPath())).toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Task fromString(String value) {
        Task task = null;
        String[] stringSplit = value.split(DELIMITER_CSV);
        try {
            int id = Integer.parseInt(stringSplit[0].trim());
            String name = stringSplit[2].trim();
            TaskStatus status = TaskStatus.valueOf(stringSplit[3].trim());
            String description = stringSplit[4].trim();

            switch (Enum.valueOf(TaskTypes.class, stringSplit[1])) {
                case TASK -> task = new Task(id, name, description, status);
                case EPIC -> task = new Epic(id, name, description, status);
                case SUBTASK -> task = new Subtask(id, name, description, status,
                        Integer.parseInt(stringSplit[5].trim()));
            }
            return task;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private void restoreSubtasksListForEpic(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        if (!epic.getEpicSubtasksIds().contains(subtask.getId())) {
            epic.addSubTaskToEpic(subtask.getId());
        }
    }

    private String toString(Task task) {
        return String.join(
                DELIMITER_CSV,
                Integer.toString(task.getId()),
                task.getTaskType().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                ""
        );
    }

    private String toString(Subtask subtask) {
        return String.join(
                DELIMITER_CSV,
                Integer.toString(subtask.getId()),
                subtask.getTaskType().toString(),
                subtask.getName(),
                subtask.getStatus().toString(),
                subtask.getDescription(),
                Integer.toString(subtask.getEpicId())
        );
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    public List<Task> getAll() {
        List<Task> allTypeTasks = new ArrayList<>();
        allTypeTasks.addAll(getAllTasks());
        allTypeTasks.addAll(getAllEpics());
        allTypeTasks.addAll(getAllSubTasks());
        return allTypeTasks;
    }
}
