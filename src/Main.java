public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Обычная таска","Сходить в магазин, купить вкусняшки");
        Epic epic = new Epic("Важный эпик 1","Сделать что то важное");
        Epic otherEpic = new Epic("Важный эпик 2","Сделать ещё что нибудь важное");
        Subtask subtask1 = new Subtask("Простая таска 1", "Сделать что то очень простое", epic);
        Subtask subtask2 = new Subtask("Простая таска 2", "Сделать что то совсем простое", epic);

        taskManager.createTask(task1);
        taskManager.createTask(epic);
        taskManager.createTask(otherEpic);
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);

        System.out.println("epic" + epic);
        System.out.println("otherEpic" + otherEpic);

        subtask1.setName("Простая таска 1 v2");
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.NEW);
        taskManager.updateTask(subtask1);
        System.out.println("epic status" + epic.getStatus());

        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(subtask1);
        System.out.println("epic status" + epic.getStatus());

        System.out.println(taskManager.getSubtasksForEpic(epic));

        taskManager.updateTask(subtask1);

        for (Task allTask : taskManager.getAllTasks()) {
            System.out.println(allTask);
        }

        System.out.println(otherEpic.getStatus());

        taskManager.deleteTaskById(2);

        for (Task allTask : taskManager.getAllTasks()) {
            System.out.println(allTask);
        }


        taskManager.deleteAllTasks();

        System.out.println(taskManager.getTaskById(1));

        for (Task allTask : taskManager.getAllTasks()) {
            System.out.println(allTask);
        }
    }
}
