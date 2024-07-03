package sg.edu.np.mad.beproductive.ToDoListPage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<ToDoModel> todoList; //list of tasks to show in recyclerView
    private TodoList activity;
    private DatabaseReference userRef;



    public ToDoAdapter(TodoList activity){
        this.activity = activity;
        this.todoList = new ArrayList<>(); //initialize

        // Initialize Firebase reference
        int user_Id = Global.getUser_Id();
        String userPath = "user" + (user_Id + 1);  // Retrieve user path from global variable
        userRef = FirebaseDatabase.getInstance().getReference("User").child(userPath).child("todo");
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // Update the task status in Firebase
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setStatus(isChecked ? 1 : 0);
                userRef.child(item.getId()).child("status").setValue(item.getStatus());
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    private boolean toBoolean(int n){
        return n == 1; // 1 = true, 0 = false
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged(); // RecyclerView updates
    }

    public Context getContext(){
        return activity;
    }

    public void deleteItem(int position) {
        // Delete a task at a specified position in the todoList and update Firebase
        ToDoModel item = todoList.get(position);
        if (item.getId() != null) {
            userRef.child(item.getId()).removeValue();
        }
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        // Open a dialog fragment to edit a task at the specified position in the todoList
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder represents each view within the RecyclerView
        CheckBox task;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todo_checkbox);
        }
    }
}