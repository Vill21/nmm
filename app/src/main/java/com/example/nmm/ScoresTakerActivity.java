package com.example.nmm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nmm.models.Scores;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoresTakerActivity extends AppCompatActivity {

    EditText editTitle, editWinner;
    ImageView imageSave;
    Scores scores;
    boolean isOldScore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores_taker);

        editTitle = findViewById(R.id.edit_text_title);
        imageSave = findViewById(R.id.imageView_save);
        editWinner = findViewById(R.id.edit_text_winner);

        scores = new Scores();
        try {
            scores = (Scores) getIntent().getSerializableExtra("old_score");
            editTitle.setText(scores.getTitle());
            editWinner.setText(scores.getWinner());
            isOldScore = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                String winner = editWinner.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(ScoresTakerActivity.this, "Please, enter the title", Toast.LENGTH_LONG).show();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM, yyyy HH:mm a");
                Date date = new Date();

                if (!isOldScore) {
                    scores = new Scores();
                }
                scores.setTitle(title);
                scores.setDate(formatter.format(date));
                scores.setScore(MainActivity.playerScore1 + "-" + MainActivity.playerScore2);
                scores.setWinner(winner);

                Intent intent = new Intent();
                intent.putExtra("score", scores);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}