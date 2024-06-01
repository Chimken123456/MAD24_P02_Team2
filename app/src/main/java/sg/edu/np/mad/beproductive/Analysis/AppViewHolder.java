package sg.edu.np.mad.beproductive.Analysis;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import sg.edu.np.mad.beproductive.R;

public class AppViewHolder extends RecyclerView.ViewHolder{
    TextView appName;
    public AppViewHolder(View itemView) {
        super(itemView);
        appName = itemView.findViewById(R.id.appName);
    }
}
