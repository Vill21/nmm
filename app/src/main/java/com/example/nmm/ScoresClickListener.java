package com.example.nmm;

import androidx.cardview.widget.CardView;

import com.example.nmm.models.Scores;

public interface ScoresClickListener {
    void onClick(Scores scores);
    void onLongClick(Scores scores, CardView cardView);
}
