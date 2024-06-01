package sg.edu.np.mad.beproductive.Analysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Array;
import java.util.ArrayList;
import sg.edu.np.mad.beproductive.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<AppViewHolder> {
    ArrayList<String> data;
    public RecyclerViewAdapter(ArrayList<String> input){
        data = input;
    }
    public AppViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType
    ){

        View item =LayoutInflater.from(parent.getContext()).inflate(
                R.layout.app_layout,
                parent,
                false
        );
        return new AppViewHolder(item);
    }
    public void onBindViewHolder(AppViewHolder holder, int position)
        {
            String s = data.get(position);
            holder.appName.setText(s);
        }
    public int getItemCount(){
        return data.size();
    }
}
