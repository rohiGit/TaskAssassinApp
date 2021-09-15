package com.example.taskassassin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskassassin.Model.Tasks;
import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Scheduler;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static java.util.Calendar.*;

public class TaskInputActivity extends AppCompatActivity {

    private static final String TAG = "Sample";

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";

    private TextView textView;
    private ImageButton saveTaskBtn;
    private String timeStamp;
    private EditText taskDetailtxt;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private DatabaseReference taskRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference userRef;
    private long pending_task;
    private TextView pendingView;
    public final static String manasia_notification_channel = "Manasia Event Reminder";
    String push_key;
    String date1;
    Intent notificationIntent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;


    String task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_input);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        saveTaskBtn = findViewById(R.id.save_btn);
        textView = findViewById(R.id.date_time_txt);
        pendingView = findViewById(R.id.pending_txt);
        taskDetailtxt = findViewById(R.id.task_detail_txt);
        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if (savedInstanceState != null) {
            // Restore value from saved state
            textView.setText(savedInstanceState.getCharSequence(STATE_TEXTVIEW));
        }

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
                    // date to timestamp
                    String str_date=myDateFormat.format(date);
                    date = (Date)myDateFormat.parse(str_date);
                    timeStamp = ""+ date.getTime();

                    // timestamp to date
                    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(date.getTime());
                    date1 = DateFormat.format("dd-MMM-yyyy hh:mm aaa", cal).toString();

                    textView.setText(date1);
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
                textView.setText("");
            }
        });

        ImageButton buttonView = findViewById(R.id.button);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Re-init each time
                dateTimeFragment.startAtCalendarView();
                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                dateTimeFragment.setDefaultDateTime(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.getTime().getMinutes()).getTime());
                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });

        saveTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirebase();

            }
        });
        //createNotificationChannel();
        int condition = getIntent().getIntExtra("condition", 0);
        if(condition == 0){
            String task = getIntent().getStringExtra("task");
            long eventInMillis = getIntent().getLongExtra("eventInMillis", 0L);
            scheduleNotification(getNotification(task), eventInMillis);
            Intent todo = new Intent(TaskInputActivity.this, TodoActivity.class);
            startActivity(todo);
        }
    }



    public void scheduleNotification(Notification notification, long futureInMillis) {

            System.out.println("entered scheduling");
            notificationIntent = new Intent(getBaseContext(), NotificationPublisher.class);
            System.out.println("still going on");
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskInputActivity.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
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

//    private void createNotificationChannel(){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//            String description = "Channel for task reminder";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel notificationChannel = new NotificationChannel(manasia_notification_channel, manasia_notification_channel, importance);
//            notificationChannel.setDescription(description);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(notificationChannel);
//
//        }
//
//    }

    private void saveToFirebase() {

        task = taskDetailtxt.getText().toString();

        if(!TextUtils.isEmpty(task) && !TextUtils.isEmpty(timeStamp)){

            Tasks tasks = new Tasks(task,timeStamp);
            push_key = taskRef.child(mFirebaseUser.getUid()).push().getKey();
            taskRef.child(mFirebaseUser.getUid()).child(push_key).setValue(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(TaskInputActivity.this, "Task is added", Toast.LENGTH_LONG).show();

//                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa");
//                    final String currentTime = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa").format(new Date());
//                    System.out.println(currentTime);
//
//                    final float dateTimeInMillis = dateTime.getTime();


                    taskRef.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pending_task = (long) dataSnapshot.getChildrenCount();
                            pending_task++;
                            userRef.child(mFirebaseUser.getUid()).child("pending_task").setValue(pending_task).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


//                            while (!stop){
//                                final String currentTime = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa").format(new Date());
//                                System.out.println(currentTime);
//                                System.out.println(date1);
//
//                                if (date1.equals(currentTime)) {
//                                    System.out.println("entered");
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                        //define the importance level of the notification
//                                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//                                        //build the actual notification channel, giving it a unique ID and name
//                                        NotificationChannel channel = new NotificationChannel(manasia_notification_channel, manasia_notification_channel, importance);
//                                        //we can optionally add a description for the channel
//                                        String description = "A channel which shows notifications about events at Manasia";
//                                        channel.setDescription(description);
//
//                                        //we can optionally set notification LED colour
//                                        channel.setLightColor(Color.MAGENTA);
//
//                                        // Register the channel with the system
//                                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().
//                                                getSystemService(Context.NOTIFICATION_SERVICE);
//                                        if (notificationManager != null) {
//                                            notificationManager.createNotificationChannel(channel);
//                                        }
//                                    }
//                                    Intent intent = new Intent(getApplicationContext(), TodoActivity.class);
//
//                                    //put together the PendingIntent
//                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, FLAG_UPDATE_CURRENT);
//                                    String notificationTitle = "You have a pending task";
//
//
//                                    String notificationText = task;
//
//                                    //build the notification
//                                    NotificationCompat.Builder notificationBuilder =
//                                            new NotificationCompat.Builder(getApplicationContext(), manasia_notification_channel)
//                                                    .setSmallIcon(R.drawable.ic_event_note_black_24dp)
//                                                    .setContentTitle(notificationTitle)
//                                                    .setContentText(notificationText)
//                                                    .setContentIntent(pendingIntent)
//                                                    .setAutoCancel(true)
//                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//                                    //trigger the notification
//                                    NotificationManagerCompat notificationManager =
//                                            NotificationManagerCompat.from(getApplicationContext());
//                                    int unique = 1;
//                                    //we give each notification the ID of the event it's describing,
//                                    //to ensure they all show up and there are no duplicates
//                                    notificationManager.notify(++unique, notificationBuilder.build());
//
//                                    stop = true;
//
//                                }
//                                //create an intent to open the event details activity
//
//
//
//                            }


                    //Alarm

//                    Intent receiverIntent = new Intent(TaskInputActivity.this, ReminderBroadcast.class);
//                    System.out.println("1 " + task);
//                    receiverIntent.putExtra("task_detail", task);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskInputActivity.this, 0, receiverIntent, 0);
//
//                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                    long timestampInMillis = Long.parseLong(timeStamp);
//                    long eventTimeStampInMillis = new Timestamp(timestampInMillis).getTime();
//                    System.out.println(eventTimeStampInMillis);
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, eventTimeStampInMillis, pendingIntent);

                    long timestampInMillis = Long.parseLong(timeStamp);
                    long eventTimeStampInMillis = new Timestamp(timestampInMillis).getTime();
                    System.out.println(task);
                    scheduleNotification(getNotification(task),  eventTimeStampInMillis);

                    Toast.makeText(TaskInputActivity.this, "Task is added", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TaskInputActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(TaskInputActivity.this, "Input invalid", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current textView
        savedInstanceState.putCharSequence(STATE_TEXTVIEW, textView.getText());
        super.onSaveInstanceState(savedInstanceState);
    }
}
