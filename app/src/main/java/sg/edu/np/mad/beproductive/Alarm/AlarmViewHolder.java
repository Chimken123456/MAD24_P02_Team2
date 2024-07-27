package sg.edu.np.mad.beproductive.Alarm;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.beproductive.R;

public class AlarmViewHolder extends RecyclerView.ViewHolder {
    TextView txt;
    Switch alarm_switch;
    LinearLayout background;
    public AlarmViewHolder(View itemview)
    {
        super(itemview);
        txt = itemview.findViewById(R.id.alarm_recycler_layout_time);
        alarm_switch = itemview.findViewById(R.id.alarm_recycler_layout_switch);
        background = itemview.findViewById(R.id.alarm_recycler_linearlayout);


    }


}
