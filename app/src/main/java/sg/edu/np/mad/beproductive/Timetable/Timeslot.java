package sg.edu.np.mad.beproductive.Timetable;

public class Timeslot {

    public int timeslot_id;
    public int getTimeslot_id() {return timeslot_id;}
    //public void setTimeslot_id(int id) {this.timeslot_id = id;}
    public String time;
    public String getTime() {return time;}
    public String description;
    public String getDescription() {return description;}
    public void setDescription(String newDesc) {this.description = newDesc;}

    public Timeslot(int id, String time, String description){
        this.time = time;
        this.description = description;
        this.timeslot_id = id;
    }
}
