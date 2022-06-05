package com.example.nmm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class EnterScreen extends AppCompatActivity implements View.OnClickListener {

    Button enterStart, enterScore, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_screen);

        enterStart = findViewById(R.id.enterStart);
        enterScore = findViewById(R.id.enterScore);
        logout = findViewById(R.id.logout);

        enterStart.setOnClickListener(this);
        enterScore.setOnClickListener(this);
        logout.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enterStart:
                startActivity(new Intent(EnterScreen.this, MainActivity.class));
                break;
            case R.id.enterScore:
                startActivity(new Intent(EnterScreen.this, ScoreTable.class));
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(EnterScreen.this, LoginActivity.class));
                break;
        }
    }
}