package com.example.taskassassin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskEditActivity extends AppCompatActivity {

    private String timeStamp;
    private String task, current_uid;
    private TextView taskText, timeText, dateText;
    private EditText addDescText;
    private String key;
    private Button saveButton, deleteButton;
    private DatabaseReference descRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        task = bundle.getString("task");
        timeStamp = bundle.getString("timestamp");
        key = bundle.getString("key");
        position = bundle.getInt("position");

        mAuth = FirebaseAuth.getInstance();
        dateText = findViewById(R.id.date_txt);
        firebaseUser = mAuth.getCurrentUser();
        current_uid = firebaseUser.getUid();
        descRef = FirebaseDatabase.getInstance().getReference().child("Tasks");
        saveButton = findViewById(R.id.save_edit_desc_btn);
        deleteButton = findViewById(R.id.delete_edit_btn);
        taskText = findViewById(R.id.task_txt);
        addDescText = findViewById(R.id.add_desc_txt);
        timeText = findViewById(R.id.time_text);

        setAttribute(task, timeStamp);

        setDescText();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(addDescText.getText())){
                    descRef.child(current_uid).child(key).child("task_description").setValue(addDescText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(TaskEditActivity.this, "Task Description Added", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descRef.child(current_uid).child(key).removeValue();
                Toast.makeText(TaskEditActivity.this, "Task Deleted", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDescText() {
        descRef.child(current_uid).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("task_description")){
                    addDescText.setText(dataSnapshot.child("task_description").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAttribute(String task, String timeStamp) {
        taskText.setText(task);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa");
        String timeString = formatter.format(new Date(Long.parseLong(timeStamp)));
        timeText.setText(timeString);

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MMM-yyyy");
        String dateString = formatterDate.format(new Date(Long.parseLong(timeStamp)));
        dateText.setText(dateString);
    }


}
