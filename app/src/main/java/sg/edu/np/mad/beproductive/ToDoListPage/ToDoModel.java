package sg.edu.np.mad.beproductive.ToDoListPage;

public class ToDoModel {
    private String id;
    private String task;
    private int status;

    public ToDoModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ToDoModel.class)
    }

    public ToDoModel(String id, String task, int status) {
        this.id = id;
        this.task = task;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
