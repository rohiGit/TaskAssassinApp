package com.example.taskassassin.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.taskassassin.R;

public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public TextView taskText;
    public TextView taskTimetxt;
    public SwipeLayout swipeLayout;
    public CardView mCenterCard;
    public FrameLayout mRightSwipeBtn;
    public FrameLayout mLeftSwipeBtn, leftNestedBtn, rightNestedBtn;
    public CardView adjustLeftCard, adjustRightCard, doneCard, deleteCard;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        taskText = itemView.findViewById(R.id.task_txt);
        doneCard = itemView.findViewById(R.id.done_card);
        deleteCard = itemView.findViewById(R.id.delete_card);
        taskTimetxt = itemView.findViewById(R.id.task_time_txt);
        swipeLayout = itemView.findViewById(R.id.single_swipe_layout);
        mCenterCard = itemView.findViewById(R.id.center_card);
        adjustLeftCard = itemView.findViewById(R.id.adjust_left);
        leftNestedBtn = itemView.findViewById(R.id.left_nested_swipe_btn);
        rightNestedBtn = itemView.findViewById(R.id.right_nested_swipe_btn);
        adjustRightCard = itemView.findViewById(R.id.adjust_right);
        mCenterCard.setOnCreateContextMenuListener(this);
        mRightSwipeBtn = itemView.findViewById(R.id.right_swipe_btn);
        mLeftSwipeBtn = itemView.findViewById(R.id.left_swipe_btn);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select menu");
        menu.add(0,0,getAdapterPosition(), "Update");

    }
}
