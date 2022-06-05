package com.example.nmm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Button reset, back;
    private EditText editEmail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        reset = findViewById(R.id.button_reset_password);
        back = findViewById(R.id.button_back_to_login_from_reset);
        editEmail = findViewById(R.id.edit_email_address_reset_password);

        reset.setOnClickListener(this);
        back.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_back_to_login_from_reset:
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                break;
            case R.id.button_reset_password:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        String email = editEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email.");
            editEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Check your email to reset the password.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Something went wrong, try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}