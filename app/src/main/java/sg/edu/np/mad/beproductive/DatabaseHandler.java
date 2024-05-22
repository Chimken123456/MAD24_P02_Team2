package sg.edu.np.mad.beproductive;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.beproductive.ToDoListPage.ToDoModel;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, " + STATUS + " INTEGER)";
    private SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TODO_TABLE);
        Log.d("DatabaseHandler", "Database created with table: " + CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase(){
        db = this.getWritableDatabase();
        Log.d("DatabaseHandler", "Database opened");
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        long result = db.insert(TODO_TABLE, null, cv);
        if (result == -1) {
            Log.e("DatabaseHandler", "Failed to insert task");
        } else {
            Log.d("DatabaseHandler", "Task inserted successfully with id: " + result);
        }
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    int idIndex = cur.getColumnIndex(ID);
                    int taskIndex = cur.getColumnIndex(TASK);
                    int statusIndex = cur.getColumnIndex(STATUS);

                    if (idIndex != -1 && taskIndex != -1 && statusIndex != -1) {
                        do {
                            ToDoModel task = new ToDoModel();
                            task.setId(cur.getInt(idIndex));
                            task.setTask(cur.getString(taskIndex));
                            task.setStatus(cur.getInt(statusIndex));
                            taskList.add(task);
                        } while (cur.moveToNext());
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHandler", "Error while retrieving tasks", e);
        } finally {
            if (cur != null) {
                cur.close();
            }
            db.endTransaction();
        }
        Log.d("DatabaseHandler", "Retrieved " + taskList.size() + " tasks");
        return taskList;
    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        int result = db.update(TODO_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
        Log.d("DatabaseHandler", "Updated status for task id " + id + " with result " + result);
    }

    public void updateTask(int id, String task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        int result = db.update(TODO_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
        Log.d("DatabaseHandler", "Updated task for id " + id + " with result " + result);
    }

    public void deleteTask(int id){
        int result = db.delete(TODO_TABLE, ID + "=?", new String[]{String.valueOf(id)});
        Log.d("DatabaseHandler", "Deleted task with id " + id + " with result " + result);
    }
}

