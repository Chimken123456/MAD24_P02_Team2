package sg.edu.np.mad.beproductive;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.beproductive.NotesPage.Note;
import sg.edu.np.mad.beproductive.NotesPage.NotesListActivity;
import sg.edu.np.mad.beproductive.NotesPage.NotesListAdapter;

public class NoteWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NoteRemoteViewsFactory(getApplicationContext());
    }
    //Implement widget service
    class NoteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context context;

        private ArrayList<Note> noteList;

        private String path;
        private DatabaseReference dbRef;

        private User user;

        public NoteRemoteViewsFactory(Context context) {
            this.context = context;
            this.noteList = new ArrayList<>();
            this.user = new User("test","test123","testingemail");
            this.path = "User/user" + String.valueOf(Global.getUser_Id()+1) + "/notes";
            this.dbRef = FirebaseDatabase.getInstance("https://madassignment-36a4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference(path);
        }

        @Override
        //Called when remoteviewfactory is first created, initialise data
        public void onCreate() {
            DatabaseHandler dbHandler = new DatabaseHandler(context);
            ArrayList<User> user_array = new ArrayList<>();
            user_array = dbHandler.getAllUsers();
            for(User u : user_array)
            {
                if(u.getSignedIn())
                {
                    user.setId(u.getId());
                    user.setName(u.getName());
                    user.setPassword(u.getPassword());
                    user.setEmail(u.getEmail());
                    user.setSignedIn(u.getSignedIn());

                }
            }

        }

        @Override
        //Called when the data in firebase changes, updates data in the stored note list
        public void onDataSetChanged() {
            dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String header = String.valueOf(item.child("header").getValue());
                            String content = String.valueOf(item.child("content").getValue());

                            Note tempNote = new Note(header, content);
                            noteList.add(tempNote);
                            Log.d("firebase",item.getKey() + " " + header + " " + content);
                            //fix update text content on save
                        }
                        Log.d("firebase", "Fetch widgettable success");
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    }
                }
            });
        }


        @Override
        public void onDestroy() {
            noteList.clear();
        }

        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notes_widget_list_item);
            Note note = noteList.get(position);

            remoteView.setTextViewText(R.id.widget_note_header, note.getNote_header());

            // Handle item click if needed
            Bundle extras = new Bundle();
            Intent fillInIntent = new Intent();

            extras.putInt("ID", Global.getUser_Id());
            extras.putString("Username", user.getName());
            extras.putString("Password", user.getPassword());
            extras.putString("Email", user.getEmail());
            extras.putInt("noteId", position);

            fillInIntent.putExtras(extras);

            remoteView.setOnClickFillInIntent(R.id.widget_note_header, fillInIntent);

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}