package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.hilo.model.UserModel;
import com.example.hilo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    private EditText txtUsername;
    private Button btnLetMeIn;
    private ProgressBar pgbLogin;
    private String phoneNumber;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        txtUsername = findViewById(R.id.edittext_login_username);
        btnLetMeIn = findViewById(R.id.button_login_let_me_in);
        pgbLogin = findViewById(R.id.progress_bar_login);

        phoneNumber = getIntent().getStringExtra("phone");
        getUsername();

        btnLetMeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUsername();
            }
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            pgbLogin.setVisibility(View.VISIBLE);
            btnLetMeIn.setVisibility(View.GONE);
        } else {
            pgbLogin.setVisibility(View.GONE);
            btnLetMeIn.setVisibility(View.VISIBLE);
        }
    }

    private void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        txtUsername.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    private void setUsername() {
        String username = txtUsername.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            txtUsername.setError("Username length must be at least 3 characters");
            return;
        }

        setInProgress(true);

        if (userModel != null) {
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}