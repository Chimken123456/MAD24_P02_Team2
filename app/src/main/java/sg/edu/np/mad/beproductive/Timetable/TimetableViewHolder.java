package sg.edu.np.mad.beproductive.Timetable;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.beproductive.R;

public class TimetableViewHolder extends RecyclerView.ViewHolder{

    TextView timeslot;
    TextView desc;
    Button edit;

    public TimetableViewHolder(View timetableView) {
        super(timetableView);
        timeslot = timetableView.findViewById(R.id.timeslotView);
        desc = timetableView.findViewById(R.id.descView);
        edit = timetableView.findViewById(R.id.editButton);
    }

}
