package sg.edu.np.mad.beproductive.NotesPage;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.beproductive.R;

public class NoteItemViewHolder extends RecyclerView.ViewHolder {

    EditText notes_field;
    EditText edit_note_header;
    TextView header;

    ImageView back;

    public NoteItemViewHolder(View noteitemView) {
        super(noteitemView);
        notes_field = noteitemView.findViewById(R.id.notes_field);
        edit_note_header = noteitemView.findViewById(R.id.edit_notes_header);
        header = noteitemView.findViewById(R.id.notes_header);
        back = noteitemView.findViewById(R.id.backbtn);
    }
}
