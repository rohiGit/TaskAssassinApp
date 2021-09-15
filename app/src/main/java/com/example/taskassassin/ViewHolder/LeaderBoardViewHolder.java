package com.example.taskassassin.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskassassin.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardViewHolder extends RecyclerView.ViewHolder {

    public TextView rankPos, rankName, rankScore;
    public CircleImageView rankThumb;

    public LeaderBoardViewHolder(@NonNull View itemView) {
        super(itemView);
        rankPos = itemView.findViewById(R.id.rank_position);
        rankName = itemView.findViewById(R.id.rank_name);
        rankScore = itemView.findViewById(R.id.rank_score);
        rankThumb = itemView.findViewById(R.id.rank_thumb);

    }
}
