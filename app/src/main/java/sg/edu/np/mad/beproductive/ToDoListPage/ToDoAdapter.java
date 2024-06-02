package sg.edu.np.mad.beproductive.ToDoListPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.DatabaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<ToDoModel> todoList; //list of tasks to show in recyclerView
    private TodoList activity;
    private DatabaseHandler db;
    public ToDoAdapter(DatabaseHandler db, TodoList activity){
        this.db = db;
        this.activity = activity;
        this.todoList = new ArrayList<>(); //initialize
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout,parent, false);
        return new ViewHolder(itemView);
    }
    public void onBindViewHolder(ViewHolder holder, int postion){
        db.openDatabase();
        ToDoModel item = todoList.get(postion);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // update the task status in the database
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.updateStatus(item.getId(), 1);
                }
                else{
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }
    public int getItemCount(){ // Returns total number of tasks in todolist
        return todoList.size();
    }
    private boolean toBoolean(int n){
        return n==1; // 1 = true, 0 = false
    }
    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged(); // recyclerview updates
    }
    public Context getContext(){
        return activity;
    }

    public void deleteItem(int position) {
        // Deletes a task at a specified position in the todoList, updates the database
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }
    public void editItem(int position){
        // Opens a dialog fragment to edit a task at the specified position in the todoList
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id",item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        // ViewHolder represents each view within the RecyclerView
        CheckBox task;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todo_checkbox);
        }

    }
}
