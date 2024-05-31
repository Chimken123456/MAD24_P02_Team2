package sg.edu.np.mad.beproductive.Reminders;

public class Reminder {
    private String title;
    private String datetime;

    public Reminder(String title, String datetime) {
        this.title = title;
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public String getDatetime() {
        return datetime;
    }
}

