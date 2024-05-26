package sg.edu.np.mad.beproductive.ToDoListPage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import sg.edu.np.mad.beproductive.Global;
import sg.edu.np.mad.beproductive.R;
import sg.edu.np.mad.beproductive.DatabaseHandler;
import sg.edu.np.mad.beproductive.User;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;
    private BroadcastReceiver receiver;
    private int user_id;
    private User user = new User("testing","testing","testing");
    private ToDoModel task;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.new_task, container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        newTaskText = getView().findViewById(R.id.newTaskTextbox);
        newTaskSaveButton = getView().findViewById(R.id.newTaskSaveBtn);
        user.setId(100);
        user_id =  Global.getUser_Id();
//        Log.i("MAOMAOO","user id test tst: " + String.valueOf(user_id1));

//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                if("ToDoList_To_AddNewTask".equals(intent.getAction()))
//                {
//                    user_id = intent.getIntExtra("ID",100);
//                    Log.i("MAOMAOO","USER ID: " + String.valueOf(user_id));
//                }
//                LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
//            }
//        };
//
//
//        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(receiver,new IntentFilter("ToDoList_To_AddNewTask"));

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if(task.length()>0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), com.google.android.material.R.color.design_default_color_primary_dark));
        }
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), com.google.android.material.R.color.design_default_color_primary_dark));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();

                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);
                } else {
                    ArrayList<User> user_array = new ArrayList<>();
                    user_array = db.getAllUsers();
                    for(User u :user_array)
                    {
                        if(u.getId() == user_id)
                        {
                            user = u;
                            break;
                        }
                    }

                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    db.insertTask(task,user.getId());


                }
//
                dismiss();
            }
        });
//        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(receiver);
    }
    @Override
    public void onDismiss(DialogInterface dialog){
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }

    }
}
