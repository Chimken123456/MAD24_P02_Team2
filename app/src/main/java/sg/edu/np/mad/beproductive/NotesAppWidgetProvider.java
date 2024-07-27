package sg.edu.np.mad.beproductive;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.np.mad.beproductive.NotesPage.Note;
import sg.edu.np.mad.beproductive.NotesPage.NoteItemActivity;
import sg.edu.np.mad.beproductive.NotesPage.NotesListActivity;
import sg.edu.np.mad.beproductive.NotesPage.NotesListAdapter;
import sg.edu.np.mad.beproductive.NotesPage.NotesListViewHolder;

/**
 * Implementation of App Widget functionality.
 */
public class NotesAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent = new Intent(context, NoteWidgetService.class);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notes_widget_layout);
        remoteViews.setRemoteAdapter(R.id.widget_note_listView, intent);
        remoteViews.setEmptyView(R.id.widget_note_listView, R.id.widget_EmptyView);

        Intent launchNoteIntent = new Intent(context, NotesListActivity.class);
        Bundle launchExtras = new Bundle();
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        ArrayList<User> user_array = new ArrayList<>();
        user_array = dbHandler.getAllUsers();

        for(User u : user_array)
        {
            if(u.getId() == Global.getUser_Id())
            {
                launchExtras.putInt("ID", u.getId());
                launchExtras.putString("Username", u.getName());
                launchExtras.putString("Password", u.getPassword());
                launchExtras.putString("Email", u.getEmail());
            }
        }

        launchNoteIntent.putExtras(launchExtras);
        PendingIntent launchPendingIntent = PendingIntent.getActivity(context, appWidgetId, launchNoteIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_add_note, launchPendingIntent);

        Intent noteIntent = new Intent(context, NoteItemActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Global.getUser_Id(), noteIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        remoteViews.setPendingIntentTemplate(R.id.widget_note_listView, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        // Retrieve and apply any saved data for the widgets being restored
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        for (int appWidgetId : newWidgetIds) {
            // Restore widget state here
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


}