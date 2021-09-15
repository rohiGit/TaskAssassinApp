package com.example.taskassassin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CharacterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_character);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        
        overridePendingTransition(R.anim.fix_anim, R.anim.slide_down);
    }
}
