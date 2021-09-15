package com.example.taskassassin;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.taskassassin.Model.Tasks;
import com.example.taskassassin.ViewHolder.TaskViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.getAvailableCalendarTypes;

public class TodoActivity extends AppCompatActivity {

    private static final String TAG = "Sample";

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    public final static String manasia_notification_channel = "Manasia Event Reminder";

    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";



    private RecyclerView mTodoRecyclerView;
    private FloatingActionButton mAddTaskBtn, mCancelBtn;
    private DatabaseReference taskRef;
    private DatabaseReference userRef;
    private  int width;
    private boolean isPositivePressed, isCancelPressed, isNegativePressed;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private FirebaseRecyclerOptions<Tasks> options;
    private FirebaseRecyclerAdapter<Tasks, TaskViewHolder> adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private TextView pendingTextView;
    private Long pending_task;
    private String timeStamp;
    private String date1;
    private int previousLength;
    private boolean backSpace;
    EditText addNewText;
    SwipeLayout swipeLayout;

    boolean isOpen = false;
    Animation close, open, cancelUp, canceDown, bringToCenter;
    private String task;
    private String push_key;
    private Intent notificationIntent;
    private DatabaseReference taskRefSave;
    private DatabaseReference userRefSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        getSupportActionBar().hide();
        open = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_open);
        canceDown  = AnimationUtils.loadAnimation(getApplication(), R.anim.cancel_down);
        cancelUp  = AnimationUtils.loadAnimation(getApplication(), R.anim.cancel_up);
        close  = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_close);
        addNewText = findViewById(R.id.add_new_txt);
        pendingTextView = findViewById(R.id.pending_txt);
        mCancelBtn = findViewById(R.id.cancel_task_btn);
        mTodoRecyclerView = findViewById(R.id.todo_recycler);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mTodoRecyclerView.setHasFixedSize(true);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddTaskBtn = findViewById(R.id.add_task_btn);
        taskRefSave = FirebaseDatabase.getInstance().getReference().child("Tasks");
        userRefSave = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid());
        userRef.keepSynced(true);
        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks").child(mFirebaseUser.getUid());
        taskRef.keepSynced(true);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Construct SwitchDateTimePicker
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    getString(R.string.clean) // Optional
            );
        }

        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm aaa", java.util.Locale.getDefault());
        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {


                try {
                    isPositivePressed = true;
                    // date to timestamp
                    String str_date=myDateFormat.format(date);
                    date = (Date)myDateFormat.parse(str_date);
                    timeStamp = ""+ date.getTime();

                    // timestamp to date
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(date.getTime());
                    date1 = DateFormat.format("dd-MMM-yyyy hh:mm aaa", cal).toString();
                    saveToFirebase();
                    addNewText.setText("");
                    addNewText.setVisibility(View.GONE);
                    mCancelBtn.setVisibility(View.INVISIBLE);
                    mCancelBtn.setEnabled(false);
                    isOpen = false;
                    View view = addNewText;
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    isPositivePressed = false;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNegativeButtonClick(Date date) {
                isNegativePressed = true;
                // Do nothing
                addNewText.setText("");
                addNewText.setVisibility(View.GONE);
                isOpen = false;
                View view = addNewText;
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                isNegativePressed = false;
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                isNegativePressed = true;
                // Do nothing
                addNewText.setText("");
                addNewText.setVisibility(View.GONE);
                isOpen = false;
                View view = addNewText;
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                isNegativePressed = false;
            }
        });
        
        
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancelPressed = true;
                addNewText.startAnimation(close);
                mCancelBtn.startAnimation(canceDown);
                addNewText.setText("");
                addNewText.setVisibility(View.GONE);
                View view = addNewText;
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                mCancelBtn.setVisibility(View.INVISIBLE);
                isOpen = false;
                addNewText.setEnabled(false);
                mCancelBtn.setEnabled(false);
                isCancelPressed = false;
            }
        });

        addNewText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
                if (!isPositivePressed && !isCancelPressed && !isNegativePressed) {
                    if (s.length() == 0) {
                        System.out.println("count is 1");
                        mCancelBtn.setVisibility(View.INVISIBLE);
                        mCancelBtn.startAnimation(canceDown);
                    }
                }
                if (backSpace){
                    mCancelBtn.setVisibility(View.INVISIBLE);
                    mCancelBtn.startAnimation(canceDown);
                    mCancelBtn.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    if (!isPositivePressed && !isCancelPressed && !isNegativePressed) {
                        mCancelBtn.setEnabled(true);
                        System.out.println("count is 0");
                        mCancelBtn.setVisibility(View.VISIBLE);
                        mCancelBtn.startAnimation(cancelUp);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                backSpace = previousLength - s.length() == 1;

                if (backSpace && !isCancelPressed) {
                    System.out.println("backspace");
                    mCancelBtn.setEnabled(true);
                    mCancelBtn.setVisibility(View.VISIBLE);
                    mCancelBtn.startAnimation(cancelUp);
                }
            }
        });

        addNewText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mCancelBtn.setEnabled(true);
                    System.out.println("focus gained");
                    mCancelBtn.setVisibility(View.VISIBLE);
                    mCancelBtn.startAnimation(cancelUp);
                }
            }
        });

        mAddTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent taskInputIntent = new Intent(TodoActivity.this, TaskInputActivity.class);
//                taskInputIntent.putExtra("condition", 101);
//                startActivity(taskInputIntent);
                if (TextUtils.isEmpty(addNewText.getText())){
                    animateFab();


            }else{
                    // Re-init each time
                    dateTimeFragment.startAtCalendarView();
                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    dateTimeFragment.setDefaultDateTime(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.getTime().getMinutes()).getTime());
                    dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
                    addNewText.startAnimation(close);
                    addNewText.setEnabled(false);
                    mCancelBtn.setVisibility(View.GONE);
                    mCancelBtn.setEnabled(false);
                }
            }
        });

        showTask();


    }

    private void saveToFirebase() {
        task = addNewText.getText().toString();

        if(!TextUtils.isEmpty(task) && !TextUtils.isEmpty(timeStamp)){
            System.out.println("time" + timeStamp);
            Tasks tasks = new Tasks(task,timeStamp);
            push_key = taskRefSave.child(mFirebaseUser.getUid()).push().getKey();
            taskRefSave.child(mFirebaseUser.getUid()).child(push_key).setValue(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(TodoActivity.this, "Task is added", Toast.LENGTH_LONG).show();

                    taskRefSave.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pending_task =  dataSnapshot.getChildrenCount();
                            System.out.println("pending" + pending_task);
                            userRefSave.child(mFirebaseUser.getUid()).child("pending_task").setValue(pending_task).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(pending_task < 2){

                                        pendingTextView.setText(pending_task + " task");
                                        System.out.println(pending_task);

                                    }else{

                                        pendingTextView.setText(pending_task + " tasks");

                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    long timestampInMillis = Long.parseLong(timeStamp);
                    long eventTimeStampInMillis = new Timestamp(timestampInMillis).getTime();
                    System.out.println(task);
                    scheduleNotification(getNotification(task),  eventTimeStampInMillis);

                    Toast.makeText(TodoActivity.this, "Task is added", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TodoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(TodoActivity.this, "Input invalid", Toast.LENGTH_LONG).show();
        }
    }

    public void scheduleNotification(Notification notification, long futureInMillis) {

        System.out.println("entered scheduling");
        notificationIntent = new Intent(getBaseContext(), NotificationPublisher.class);
        System.out.println("still going on");
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoActivity.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }

    public Notification getNotification(String content) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            System.out.println("entered");
            //define the importance level of the notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            //build the actual notification channel, giving it a unique ID and name
            NotificationChannel channel = new NotificationChannel(manasia_notification_channel, manasia_notification_channel, importance);

            //we can optionally add a description for the channel
            String description = "A channel which shows notifications about events at Manasia";
            channel.setDescription(description);


            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                System.out.println("channel made");
            }
        }
        Intent intent = new Intent(getApplicationContext(), TodoActivity.class);
        //put together the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), manasia_notification_channel)
                .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                .setContentTitle("You have a pending task..")
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        return notificationBuilder.build();
    }

    private void animateFab() {
        if (isOpen){
            addNewText.startAnimation(close);
            addNewText.setVisibility(View.INVISIBLE);
            addNewText.setEnabled(false);
            mCancelBtn.startAnimation(canceDown);
            mCancelBtn.setVisibility(View.INVISIBLE);
            mCancelBtn.setEnabled(false);
            View view = addNewText;
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            isOpen = false;
        }else{
            mCancelBtn.startAnimation(cancelUp);
            mCancelBtn.setVisibility(View.VISIBLE);
            mCancelBtn.setEnabled(true);
            addNewText.setEnabled(true);
            addNewText.setVisibility(View.VISIBLE);
            addNewText.startAnimation(open);
            isOpen = true;
        }
    }

    private void deleteTask(String key, final int position){
        taskRef.child(key).removeValue();
        taskRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyItemRemoved(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showTask() {

        options = new FirebaseRecyclerOptions.Builder<Tasks>()
                .setQuery(taskRef, Tasks.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Tasks, TaskViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position, @NonNull final Tasks model) {
                holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                holder.taskText.setText(model.getTask());
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                System.out.println(model.getTask());
                cal.setTimeInMillis(Long.parseLong(model.getTimestamp()));
                String date1 = DateFormat.format("hh:mm aaa", cal).toString();
                holder.taskTimetxt.setText(date1);

                holder.adjustLeftCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteTask(adapter.getRef(position).getKey(), position);

                        userRef.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long score = (long) dataSnapshot.getValue();
                                score += 100;
                                userRef.child("score").setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                pending_task = dataSnapshot.getChildrenCount();
                                                if(pending_task < 2){

                                                    pendingTextView.setText(pending_task + " task");
                                                    System.out.println(pending_task);

                                                }else{

                                                    pendingTextView.setText(pending_task + " tasks");

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holder.adjustRightCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(adapter.getRef(position).getKey());
                        deleteTask(adapter.getRef(position).getKey(), position);
                        userRef.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long score = (long) dataSnapshot.getValue();
                                score -= 50;
                                userRef.child("score").setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                pending_task = dataSnapshot.getChildrenCount();
                                                if(pending_task < 2){

                                                    pendingTextView.setText(pending_task + " task");
                                                    System.out.println(pending_task);

                                                }else{

                                                    pendingTextView.setText(pending_task + " tasks");

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holder.leftNestedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTask(adapter.getRef(position).getKey(), position);
                        userRef.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long score = (long) dataSnapshot.getValue();
                                score += 100;
                                userRef.child("score").setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                pending_task = dataSnapshot.getChildrenCount();
                                                if(pending_task < 2){

                                                    pendingTextView.setText(pending_task + " task");
                                                    System.out.println(pending_task);

                                                }else{

                                                    pendingTextView.setText(pending_task + " tasks");

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                holder.mCenterCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent taskEditIntent = new Intent(TodoActivity.this, TaskEditActivity.class);
                        taskEditIntent.putExtra("task", model.getTask());
                        taskEditIntent.putExtra("timestamp", model.getTimestamp());
                        taskEditIntent.putExtra("key", adapter.getRef(position).getKey());
                        taskEditIntent.putExtra("position", position);
                        startActivity(taskEditIntent);
                    }
                });
                holder.rightNestedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(adapter.getRef(position).getKey());
                        deleteTask(adapter.getRef(position).getKey(), position);
                        userRef.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long score = (long) dataSnapshot.getValue();
                                score -= 50;
                                userRef.child("score").setValue(score).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                pending_task = dataSnapshot.getChildrenCount();
                                                if(pending_task < 2){

                                                    pendingTextView.setText(pending_task + " task");
                                                    System.out.println(pending_task);

                                                }else{

                                                    pendingTextView.setText(pending_task + " tasks");

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_todo_layout
                ,parent, false);
                return new TaskViewHolder(itemView);
            }
        };

        mTodoRecyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pending_task = (long) dataSnapshot.getChildrenCount();
                if(pending_task < 2){

                    pendingTextView.setText(pending_task + " task");
                    System.out.println(pending_task);

                }else{

                    pendingTextView.setText(pending_task + " tasks");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("Update")){

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, Tasks item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("Please update the fields");

        View update_layout = LayoutInflater.from(this).inflate(R.layout.custom_update_layout, null);
        final EditText updateTasktxt = update_layout.findViewById(R.id.update_task_detail_txt);
        ImageButton updateDatebtn = update_layout.findViewById(R.id.update_time_button);
        final TextView updateDatetxt = update_layout.findViewById(R.id.update_date_time_txt);

        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    getString(R.string.clean) // Optional
            );
        }

        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm aaa", java.util.Locale.getDefault());
        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {


                try {
                    // date to timestamp
                    String str_date=myDateFormat.format(date);
                    date = (Date)myDateFormat.parse(str_date);
                    timeStamp = ""+ date.getTime();

                    // timestamp to date
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(date.getTime());
                    date1 = DateFormat.format("dd-MMM-yyyy hh:mm aaa", cal).toString();

                    updateDatetxt.setText(date1);
                    System.out.println("1" + date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                updateDatetxt.setText("");
            }
        });


        updateDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Re-init each time
                dateTimeFragment.startAtCalendarView();
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                dateTimeFragment.setDefaultDateTime(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.getTime().getMinutes()).getTime());
                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });

        updateTasktxt.setText(item.getTask());

//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        String dateString = formatter.format(new Date(Long.parseLong(item.getTimestamp())));
//
//        updateDatetxt.setText(dateString);


        builder.setView(update_layout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String task = updateTasktxt.getText().toString();
                String dateTimestamp = "";

                System.out.println(date1);
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa");
                try {
                    Date date = df.parse(date1);
                    dateTimestamp = "" + date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Tasks tasks = new Tasks(task, dateTimestamp);
                long eventInMillis = new Timestamp(Long.parseLong(dateTimestamp)).getTime();
                scheduleNotification(getNotification(task), eventInMillis);
                taskRef.child(key).setValue(tasks);
                Toast.makeText(TodoActivity.this, "Task is updated", Toast.LENGTH_LONG).show();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
