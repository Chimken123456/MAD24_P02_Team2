package sg.edu.np.mad.beproductive.Timetable;

public class Timeslot {

    public String time;
    public String getTime() {return time;}
    public String description;
    public String getDescription() {return description;}
    public void setDescription(String newDesc) {this.description = newDesc;}

    public Timeslot(String time, String description){
        this.time = time;
        this.description = description;
    }
}
