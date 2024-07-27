package sg.edu.np.mad.beproductive.Alarm;

import java.io.Serializable;
import java.util.Date;

public class Alarm {

    private String time;
    private Boolean checked;
    private Integer alarm_id;


    public String getTime(){return time;}
    public void setTime(String t){time = t;}

    public Boolean getChecked(){return checked;}
    public void setChecked(Boolean c){checked = c;}

    public Integer getAlarm_id(){return alarm_id;}
    public void setAlarm_id(Integer i){alarm_id = i;}

    public Alarm(String t , Boolean c)
    {
        time = t;
        checked = c;
    }

}
