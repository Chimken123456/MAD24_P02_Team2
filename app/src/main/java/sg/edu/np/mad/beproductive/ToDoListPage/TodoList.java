package sg.edu.np.mad.beproductive.ToDoListPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.HomePage.HomeMenu;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.User;

public class TodoList extends AppCompatActivity  implements  DialogCloseListener{

    private RecyclerView taskRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_todo);

//        Intent recievingEnd = getIntent();
//        int id = recievingEnd.getIntExtra("ID",0);
//        String username = recievingEnd.getStringExtra("Username");
//        String password = recievingEnd.getStringExtra("Password");
//        String email = recievingEnd.getStringExtra("Email");
//        User user0 = new User(username,password,email);
//        user0.setId(id);

        db = new DatabaseHandler(this);
        db.openDatabase();

        taskList = new ArrayList<>();

        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db, this);
        taskRecyclerView.setAdapter(tasksAdapter);

        fab = findViewById(R.id.addtaskFAB);


        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);


        taskList = db.getAllTasks(Global.getUser_Id());
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(),AddNewTask.TAG);

            }
        });
        ImageView backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recievingEnd = getIntent();
                int id = recievingEnd.getIntExtra("ID", 0);
                String username = recievingEnd.getStringExtra("Username");
                String password = recievingEnd.getStringExtra("Password");
                String email = recievingEnd.getStringExtra("Email");

                User user0 = new User(username, password, email);
                user0.setId(id);

                Global.setUser_Id(user0.getId());

                Intent intent = new Intent(TodoList.this, HomeMenu.class);
                intent.putExtra("ID", user0.getId());
                intent.putExtra("Username", user0.getName());
                intent.putExtra("Password", user0.getPassword());
                intent.putExtra("Email", user0.getEmail());
                startActivity(intent);
                finish(); // Call this if you don't want to keep the current activity in the back stack
            }
        });

//        Intent intent = new Intent("ToDoList_To_AddNewTask");
//        Bundle extras = new Bundle();
//        extras.putInt("ID",user0.getId());
//        extras.putString("Username",user0.getName());
//        extras.putString("Password",user0.getPassword());
//        extras.putString("Email",user0.getEmail());
//        extras.putBoolean("SignUp",true);
//        intent.putExtras(extras);
////        Log.i("MAOMAOO","todolist java "+ String.valueOf(user0.getId()));
//
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override public void handleDialogClose(DialogInterface dialog){
        Log.i("MAOMAOO","testing");
        taskList = db.getAllTasks(Global.getUser_Id());
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
//        Intent recievingEnd = getIntent();
//        int id = recievingEnd.getIntExtra("ID",0);
//        String username = recievingEnd.getStringExtra("Username");
//        String password = recievingEnd.getStringExtra("Password");
//        String email = recievingEnd.getStringExtra("Email");
//        User user0 = new User(username,password,email);
//        user0.setId(id);
//
//        Intent intent = new Intent("ToDoList_To_AddNewTask");
//        Bundle extras = new Bundle();
//        extras.putInt("ID",user0.getId());
//        extras.putString("Username",user0.getName());
//        extras.putString("Password",user0.getPassword());
//        extras.putString("Email",user0.getEmail());
//        extras.putBoolean("SignUp",true);
//        intent.putExtras(extras);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}
