package com.example.nmm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.nmm.adapters.ScoresListAdapter;
import com.example.nmm.database.RoomDB;
import com.example.nmm.models.Scores;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ScoreTable extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    ScoresListAdapter scoresListAdapter;
    List<Scores> scoresList = new ArrayList<>();
    RoomDB database;
    Scores selectedScore;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    private final ScoresClickListener scoresClickListener = new ScoresClickListener() {
        @Override
        public void onClick(Scores scores) {
            Intent intent = new Intent(ScoreTable.this, ScoresTakerActivity.class);
            intent.putExtra("old_score", scores);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Scores scores, CardView cardView) {
            selectedScore = new Scores();
            selectedScore = scores;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Scores new_scores = (Scores) data.getSerializableExtra("score");
                database.mainDAO().update(new_scores.getID(), new_scores.getTitle(), new_scores.getWinner());
                scoresList.clear();
                scoresList.addAll(database.mainDAO().getAll());

                String email = mAuth.getCurrentUser().getEmail();
                if (email != null) {
                    User user = new User(email, database.mainDAO().getAll());
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(mAuth.getCurrentUser().getUid())
                            .setValue(user);
                }

                scoresListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);

        recyclerView = findViewById(R.id.recycler_home);

        database = RoomDB.getInstance(this);
        scoresList = database.mainDAO().getAll();

        ImageView returnFromScoreTable = findViewById(R.id.return_from_score_table);
        returnFromScoreTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreTable.this, EnterScreen.class);
                startActivity(intent);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        ImageView pullData = findViewById(R.id.pull_data);
        pullData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = user.getUid();
                reference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().getValue(User.class);

                            if (user != null) {
                                scoresList = user.scoresList;
                                for (Scores score : scoresList) {
                                    database.mainDAO().insert(score);
                                }
                                scoresListAdapter.notifyDataSetChanged();

                                Toast.makeText(ScoreTable.this, "Downloaded!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ScoreTable.this, "Something happened while checking data in the database", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        updateRecycler(scoresList);
    }

    private void collectChangesOnTheRun() {

        // user = FirebaseAuth.getInstance().getCurrentUser();
        // mAuth = FirebaseAuth.getInstance();
        // reference = FirebaseDatabase.getInstance().getReference("users");
        String userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    scoresList = userProfile.scoresList;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScoreTable.this, "Something happened while checking data in the database", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateRecycler(List<Scores> scoresList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        scoresListAdapter = new ScoresListAdapter(this, scoresList, scoresClickListener);
        recyclerView.setAdapter(scoresListAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.delete) {
            database.mainDAO().delete(selectedScore);
            scoresList.remove(selectedScore);

            String email = mAuth.getCurrentUser().getEmail();
            if (email != null) {
                User user = new User(email, database.mainDAO().getAll());
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(mAuth.getCurrentUser().getUid())
                        .setValue(user);

            scoresListAdapter.notifyDataSetChanged();
            }

            return true;
        }
        return false;
    }
}