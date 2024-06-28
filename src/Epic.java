import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> epicSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }
    //привязанные к эпику сабтаски хранятся в его списке
    public ArrayList<Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }
    // при создании таски добавляем эту задачу в эпик в конструкторе сабтаски
    public void joinSubTaskToEpic(Subtask subtask) {
        epicSubtasks.add(subtask);
    }

    @Override
    public void setStatus(TaskStatus status) {
        // статус эпика обновляется исходя из статусов его подзадач
        if (epicSubtasks.isEmpty() || epicSubtasks.stream().allMatch(x -> x.getStatus().equals(TaskStatus.NEW))) {
            this.status = TaskStatus.NEW;
            return;
        }
        if (epicSubtasks.stream()
                .allMatch(x -> x.getStatus().equals(TaskStatus.DONE))) {
            this.status = TaskStatus.DONE;
            return;
        }
        this.status = TaskStatus.IN_PROGRESS;
    }
}

