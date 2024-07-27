package sg.edu.np.mad.beproductive.NotesPage;

import android.text.Editable;
import android.text.TextWatcher;

public class CustomTextWatcher implements TextWatcher {
    Boolean wasEdited = false;
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (wasEdited) {
            wasEdited = false;
            return;
        }

        // prevent infinite loop
        wasEdited = true;
    }
}
