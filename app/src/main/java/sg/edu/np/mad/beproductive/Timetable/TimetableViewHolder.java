package sg.edu.np.mad.beproductive.Timetable;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import sg.edu.np.mad.beproductive.R;

public class TimetableViewHolder extends RecyclerView.ViewHolder{

    TextView timeslot;
    TextView desc;
    TextView edit;
    //Search for the correct views to reference
    public TimetableViewHolder(View timetableView) {
        super(timetableView);
        timeslot = timetableView.findViewById(R.id.timeslotView);
        desc = timetableView.findViewById(R.id.descView);
        edit = timetableView.findViewById(R.id.editButton);
    }

}
