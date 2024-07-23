package sg.edu.np.mad.beproductive.Reminders;

public class Reminder {
    private int id;  // Add this field
    private String title;
    private String datetime;
    private String type;

    // Default constructor
    public Reminder() {
    }

    // Updated constructor to include id
    public Reminder(int id, String title, String datetime, String type) {
        this.id = id;
        this.title = title;
        this.datetime = datetime;
        this.type = type;
    }

    // Overloaded constructor for new reminders without id
    public Reminder(String title, String datetime, String type) {
        this.title = title;
        this.datetime = datetime;
        this.type = type;
    }

    // Getter and setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


