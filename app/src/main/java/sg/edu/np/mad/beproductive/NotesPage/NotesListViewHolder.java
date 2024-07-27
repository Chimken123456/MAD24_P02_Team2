package sg.edu.np.mad.beproductive.NotesPage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.beproductive.R;

public class NotesListViewHolder extends RecyclerView.ViewHolder{

    TextView header;
    ImageView edit;
    ImageView delete;
    //Search for the correct views to reference
    public NotesListViewHolder(View notelistView) {
        super(notelistView);
        header = notelistView.findViewById(R.id.notes_header);
        edit = notelistView.findViewById(R.id.edit_note);
        delete = notelistView.findViewById(R.id.delete_note);
    }

}
