package sg.edu.np.mad.beproductive;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import sg.edu.np.mad.beproductive.ToDoListPage.ToDoModel;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String ID_USER = "user_id";
    private static String USER_ID = "user_id";
    private static final String USER_TABLE = "users";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, " + STATUS + " INTEGER," + ID_USER + " INTEGER," + " FOREIGN KEY ("+USER_ID+") REFERENCES " + USER_TABLE + "("+USER_ID+")" + ")";

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String SIGNED_IN = "signed_in";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + "(" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USERNAME + " TEXT, " + EMAIL + " TEXT, " + PASSWORD + " TEXT, "+ SIGNED_IN+ " TEXT DEFAULT \"false\""+ ")";

    private static String TIMESLOT_ID = "timeslot_id";
    private static String TIMESLOT = "timeslot";
    private static String DESC = "description";
    private static final String SCHEDULE_TABLE = "schedule";
    private static final String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + SCHEDULE_TABLE + "(" + TIMESLOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIMESLOT + " TEXT, " + DESC + " TEXT " +")";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TODO_TABLE);
        Log.d("DatabaseHandler", "Database created with table: " + CREATE_TODO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
//        if(newVersion > oldVersion)
//        {
//            db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE);
//        }
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    public void openDatabase(){
        db = this.getWritableDatabase();
        Log.d("DatabaseHandler", "Database opened");
    }

    public void insertTask(ToDoModel task, int userId){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(ID_USER,userId);
        long result = db.insert(TODO_TABLE, null, cv);
        if (result == -1) {
            Log.e("DatabaseHandler", "Failed to insert task");
        } else {
            Log.d("DatabaseHandler", "Task inserted successfully with id: " + result);
        }
    }

    public List<ToDoModel> getAllTasks(int id) {
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
                    int userIndex = cur.getColumnIndex(ID_USER);
                    if (idIndex != -1 && taskIndex != -1 && statusIndex != -1) {
                        do {
//                            Log.i("MAOMAOO","database "+String.valueOf(id) + "   " + String.valueOf(cur.getInt(userIndex)));
                            if(id == cur.getInt(userIndex))
                            {
                                ToDoModel task = new ToDoModel();
                                task.setId(cur.getInt(idIndex));
                                task.setTask(cur.getString(taskIndex));
                                task.setStatus(cur.getInt(statusIndex));
                                task.setUser_id(cur.getInt(userIndex));
                                taskList.add(task);
                            }

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

    public void addUsers(User user)
    {
        ContentValues values = new ContentValues();
        values.put(USERNAME,user.getName());
        values.put(EMAIL,user.getEmail());
        values.put(PASSWORD,user.getPassword());
        values.put(SIGNED_IN,false);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(USER_TABLE,null,values);
    }
    public ArrayList<User> getAllUsers()
    {
        ArrayList<User> user_array = new ArrayList<>();
        int id;
        String username;
        String email;
        String password;
        String signedin;
        String query = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =db.rawQuery(query,null);
        //If it exists
        if(cursor.moveToFirst())
        {
            id = Integer.parseInt(cursor.getString(0));
            username =cursor.getString(1);
            email = cursor.getString(2);
            password = cursor.getString(3);
            signedin = cursor.getString(4);

            User user =new User(username,password,email);
            if(signedin.equals("1"))
            {
                user.setSignedIn(true);
            }
            else
            {
                user.setSignedIn(false);
            }
            user.setId(id);

            user_array.add(user);
        }
        while(cursor.moveToNext())
        {
            id = Integer.parseInt(cursor.getString(0));
            username =cursor.getString(1);
            email = cursor.getString(2);
            password = cursor.getString(3);
            signedin = cursor.getString(4);
            User user =new User(username,password,email);
            user.setId(id);
            if(signedin.equals("1"))
            {
                user.setSignedIn(true);
            }
            else
            {
                user.setSignedIn(false);
            }
            user_array.add(user);

        }
        cursor.close();
        return user_array;
    }
    public void updateSignedIn_User(boolean signedIn,int userId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SIGNED_IN,signedIn);
        db.update(USER_TABLE,values,USER_ID + "= ?", new String[]{String.valueOf(userId)});
    }

}

