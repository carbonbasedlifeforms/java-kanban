public class Subtask extends Task{
    Epic epic;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        epic.joinSubTaskToEpic(this);
    }

    public Epic getEpic() {
        return epic;
    }
}
