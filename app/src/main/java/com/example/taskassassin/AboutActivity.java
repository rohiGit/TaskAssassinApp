package com.example.taskassassin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Set;

public class AboutActivity extends AppCompatActivity {

    private ImageView appSwipeUp, designerSwipeUp;
    private LinearLayout  designerPanelDown, designerPanelUp, linearAppUp;
    private SlidingUpPanelLayout slidingUpPanelLayout, devUpPanelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        designerPanelUp = findViewById(R.id.linear_designer_up);
        appSwipeUp = findViewById(R.id.app_swipe_up);
        designerSwipeUp = findViewById(R.id.designer_swipe_up);
        linearAppUp = findViewById(R.id.linear_app_up);
        designerPanelDown = findViewById(R.id.designer_panel_down);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        devUpPanelLayout = findViewById(R.id.dev_slider);

        setListeners();
    }

    private void setListeners() {
        appSwipeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        designerSwipeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        designerPanelDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        linearAppUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        designerPanelUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        devUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.DRAGGING){
                    slidingUpPanelLayout.setDragView(null);
                }
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    slidingUpPanelLayout.setDragView(R.id.dev_slider);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fix_anim, R.anim.slide_out_left);
    }
}
