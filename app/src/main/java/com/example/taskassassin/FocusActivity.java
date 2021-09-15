package com.example.taskassassin;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class FocusActivity extends AppCompatActivity {

    private CardView fifteenMinCard, twentyCard, thirtyCard, fortyCard, hourCard, fifteenBorder, twentyBorder, thirtyBorder, fortyBorder, hourBorder;
    int selected_index = 0;
    private Button nextButton;
    TextView timerTxt;
    CardView cardViewBorder , cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_focus);
        fifteenMinCard = findViewById(R.id.fifteen_min_card);
        timerTxt = findViewById(R.id.timer_text);
        twentyCard = findViewById(R.id.twenty_min_card);
        nextButton = findViewById(R.id.next_btn);
        thirtyCard = findViewById(R.id.thirty_min_card);
        fortyCard = findViewById(R.id.forty_min_card);
        hourCard = findViewById(R.id.hour_card);
        fifteenBorder = findViewById(R.id.fifteen_min_card_border);
        twentyBorder = findViewById(R.id.twenty_min_card_border);
        thirtyBorder = findViewById(R.id.thirty_min_card_border);
        fortyBorder = findViewById(R.id.forty_min_card_border);
        hourBorder = findViewById(R.id.hour_card_border);

        fifteenMinCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fifteenBorder.getVisibility() == View.INVISIBLE) {
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorGreen, getTheme()));
                    if (selected_index != 0) {
                        getCardSelected(selected_index).setVisibility(View.INVISIBLE);
                    }
                    fifteenBorder.setVisibility(View.VISIBLE);
                    selected_index = 1;
                }else {
                    selected_index = 0;
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorDarkGreen, getTheme()));
                    fifteenBorder.setVisibility(View.INVISIBLE);
                }
                timerTxt.setText("00:15:00");
            }
        });

        twentyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (twentyBorder.getVisibility() == View.INVISIBLE) {
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorGreen, getTheme()));
                    if (selected_index != 0) {
                        getCardSelected(selected_index).setVisibility(View.INVISIBLE);
                    }
                    twentyBorder.setVisibility(View.VISIBLE);
                    selected_index = 2;
                }else {
                    selected_index = 0;
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorDarkGreen, getTheme()));
                    twentyBorder.setVisibility(View.INVISIBLE);
                }
                timerTxt.setText("00:20:00");
            }
        });

        thirtyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thirtyBorder.getVisibility() == View.INVISIBLE) {
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorGreen, getTheme()));
                    if (selected_index != 0) {
                        getCardSelected(selected_index).setVisibility(View.INVISIBLE);
                    }
                    thirtyBorder.setVisibility(View.VISIBLE);
                    selected_index = 3;
                }else {
                    selected_index = 0;
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorDarkGreen,getTheme() ));
                    thirtyBorder.setVisibility(View.INVISIBLE);
                }
                timerTxt.setText("00:30:00");
            }
        });

        fortyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fortyBorder.getVisibility() == View.INVISIBLE) {
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorGreen, getTheme()));
                    if (selected_index != 0) {
                        getCardSelected(selected_index).setVisibility(View.INVISIBLE);
                    }
                    fortyBorder.setVisibility(View.VISIBLE);
                    selected_index = 4;
                }else {
                    selected_index = 0;
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorDarkGreen, getTheme()));
                    fortyBorder.setVisibility(View.INVISIBLE);
                }
                timerTxt.setText("00:45:00");
            }
        });

        hourCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hourBorder.getVisibility() == View.INVISIBLE) {
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorGreen, getTheme()));
                    if (selected_index != 0) {
                        getCardSelected(selected_index).setVisibility(View.INVISIBLE);
                    }
                    hourBorder.setVisibility(View.VISIBLE);
                    selected_index = 5;
                }else {
                    selected_index = 0;
                    nextButton.getBackground().setTint(getResources().getColor(R.color.colorDarkGreen, getTheme()));
                    hourBorder.setVisibility(View.INVISIBLE);
                }
                timerTxt.setText("01:00:00");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_index != 0){
                getCardSelected(selected_index).setVisibility(View.INVISIBLE);
                Intent startFocusIntent = new Intent(FocusActivity.this, FocusStartActivity.class);
                startFocusIntent.putExtra("duration", selected_index);
                Pair[] pairs = new Pair[2];
                System.out.println(selected_index);
                pairs[0] = new Pair<View, String>(getCardView(getCardSelected(selected_index)), "cardTransition");
                pairs[1] = new Pair<View, String>(timerTxt, "timerTransition");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(FocusActivity.this, pairs);
                startActivity(startFocusIntent, activityOptions.toBundle());
                }
            }
        });
    }

    public CardView getCardSelected(int index){


        if(selected_index != 0){
            if (index == 1) {
                cardViewBorder = fifteenBorder;
            }else if (index == 2){
                cardViewBorder = twentyBorder;
            }else if (index ==  3){
                cardViewBorder = thirtyBorder;
            }else if (index == 4){
                cardViewBorder = fortyBorder;
            }else if (index == 5){
                cardViewBorder = hourBorder;
            }
        }
        return cardViewBorder;
    }

    public CardView getCardView(CardView borderCard){
        if (borderCard == fifteenBorder){
            cardView = fifteenMinCard;
        }else if (borderCard == twentyBorder){
            cardView = twentyCard;
        }else if (borderCard == thirtyBorder){
            cardView = thirtyCard;
        }else if (borderCard == fortyBorder){
            cardView = fortyCard;
        }else if (borderCard == hourBorder){
            cardView = hourCard;
        }
        return cardView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        nextButton.getBackground().setTint(getResources().getColor(R.color.colorDarkGreen, getTheme()));
        selected_index = 0;
        timerTxt.setText("00:00:00");
    }
}
