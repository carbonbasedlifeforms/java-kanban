import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public static int taskCounter;
    HashMap<Integer, Task> tasks = new HashMap<>();

    // получить список всех задач (объектов)
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> outputTasks = new ArrayList<>();
        for (int i : tasks.keySet()) {
            outputTasks.add(tasks.get(i));
        }
        return outputTasks;
    }

    // удалить все задачи
    public void deleteAllTasks() {
        tasks.clear();
    }

    // получить задачу по Id
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // создать задачу, в качестве параметра передается сам объект
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // удалить задачу по Id
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // получить список сабтасок из эпика
    public ArrayList<Subtask> getSubtasksForEpic(Task task) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<Subtask>();
        if (task instanceof Epic) {
            subtasksForEpic = ((Epic) task).getEpicSubtasks();
        } else {
            System.out.println("Данная задача не является эпиком");
        }
        return subtasksForEpic;
    }

    // обновление задачи, в качестве параметра передается сам объект,
    public void updateTask (Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет");
            return;
        }
        if (task instanceof Subtask) {
            ((Subtask) task).getEpic().setStatus(task.getStatus());
        }
        tasks.replace(task.getId(), task);
    }
}
