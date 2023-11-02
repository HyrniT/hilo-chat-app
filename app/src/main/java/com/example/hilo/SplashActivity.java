package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FirebaseUtil.isLoggedIn() && getIntent().getExtras() != null) {
            // From notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.getUsersCollection().document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        UserModel model = task.getResult().toObject(UserModel.class);

                        // Start the MainActivity first then ChatActivity (use for "back" button
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        SplashActivity.this.startActivity(mainIntent);

                        Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
                        AndroidUtil.passUserModel(intent, model);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        SplashActivity.this.startActivity(intent);
                        finish();
                    }
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtil.isLoggedIn()) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                    }
                    finish();
                }
            }, 2000);
        }
    }
}