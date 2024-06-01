package sg.edu.np.mad.beproductive.Reminders;

public class Reminder {
    private String title;
    private String datetime;
    private String type;

    public Reminder(String title, String datetime, String type) {
        this.title = title;
        this.datetime = datetime;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getType() {
        return type;
    }
}

