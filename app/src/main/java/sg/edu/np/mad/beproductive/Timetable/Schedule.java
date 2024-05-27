package sg.edu.np.mad.beproductive.Timetable;

import java.util.ArrayList;

public class Schedule {

    public ArrayList<Timeslot> timeslots = new ArrayList<Timeslot>();
    public void addTimeslot(Timeslot item) { this.timeslots.add(item); }
    public ArrayList<Timeslot> getTimeslots() {
        return timeslots;
    }
    public void onCreate() {
        String tempDesc = "No Activity";
        for (int i = 0; i < 24; i++) {
            Timeslot newTimeslot;
            if (i < 10) {
                newTimeslot = new Timeslot("0"+ i + "00", tempDesc);
            }
            else { newTimeslot = new Timeslot(i + "00", tempDesc); }

            addTimeslot(newTimeslot);
        }
    }




}
