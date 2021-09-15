package com.example.taskassassin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

public class FocusStartActivity extends AppCompatActivity {


    private enum TimerStatus {
        STARTED,
        STOPPED
    }
    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private CountDownTimer countDownTimer;
    private Button startStopBtn;
    private CardView storyCard;
    private int selected_index;
    private long setTimer;
    private Drawable progressRed, progressOrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_start);
        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();
        selected_index = bundle.getInt("duration");
        initViews();
        getTimerText();
        getTimeInMillis();
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerStatus == TimerStatus.STOPPED) {
                    progressBarCircle.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.colorGreen), android.graphics.PorterDuff.Mode.SRC_IN);
                    textViewTime.setTextColor(getResources().getColor(R.color.colorGreen));
                    startStopBtn.getBackground().setTint(getResources().getColor(R.color.colorGreen));
//                    progressBarCircle.setProgressDrawable(pr);
                    startStop();
                }else
                    stopTimer();
                }


        });

    }



    private void getTimerText() {
        if (selected_index == 1){
            textViewTime.setText("00:15:00");
        }else if (selected_index == 2){
            textViewTime.setText("00:20:00");
        }else if (selected_index == 3){
            textViewTime.setText("00:30:00");
        }else if (selected_index == 4){
            textViewTime.setText("00:45:00");
        }else if (selected_index == 5){
            textViewTime.setText("01:00:00");
        }
    }


    private void stopTimer() {
        progressBarCircle.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN);
        countDownTimer.cancel();
        timerStatus = TimerStatus.STOPPED;
        textViewTime.setTextColor(getResources().getColor(R.color.colorRed));
        getTimerText();
        startStopBtn.getBackground().setTint(getResources().getColor(R.color.colorRed));
        startStopBtn.setText("Start again");
    }


    private void startStop() {
            startStopBtn.setText("Stop");
            System.out.println(selected_index);
            // call to initialize the timer values
            getTimeInMillis();
            System.out.println(setTimer);
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            // changing play icon to stop icon
            // making edit text not editable
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

    }


    private void startCountDownTimer() {
        System.out.println(setTimer);
        countDownTimer = new CountDownTimer(setTimer, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

                textViewTime.setText("Congratulations you did it!");
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                // changing stop icon to start icon
                // making edit text editable
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    private void setProgressBarValues() {

        progressBarCircle.setMax((int) setTimer / 1000);
        progressBarCircle.setProgress((int) setTimer / 1000);
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    private void getTimeInMillis() {
        if (selected_index == 1){
            setTimer = 15*60*1000;
        }else if (selected_index == 2){
            setTimer = 20*60*1000;
        }else if (selected_index == 3){
            setTimer = 30*60*1000;
        }else if (selected_index == 4){
            setTimer = 45*60*1000;
        }else if (selected_index == 5){
            setTimer = 60*60*1000;
        }
    }


    private void initViews() {
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        storyCard = findViewById(R.id.story_card);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        startStopBtn =  findViewById(R.id.start_stop_btn);
        progressOrange = ContextCompat.getDrawable(this, R.drawable.drawable_circle_yellow);
        progressRed = ContextCompat.getDrawable(this, R.drawable.drawable_circle_red);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
