package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ChatInfoActivity extends AppCompatActivity {
    private ImageView imvAvatar;
    private RelativeLayout btnPinMessages, btnMediaStore;
    private TextView txtUsername, txtPhone;
    private ImageButton btnBack;
    private String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        txtUsername = findViewById(R.id.txtUsername);
        txtPhone = findViewById(R.id.txtPhone);
        imvAvatar = findViewById(R.id.imvAvatar);
        btnPinMessages = findViewById(R.id.btnPinMessages);
        btnMediaStore = findViewById(R.id.btnMediaStore);
        btnBack = findViewById(R.id.btnBack);

        chatroomId = getIntent().getStringExtra("chatroomId");
        String otherUserId = getIntent().getStringExtra("otherUserId");
        String otherUsername = getIntent().getStringExtra("otherUsername");
        String otherPhone = getIntent().getStringExtra("otherPhone");

        txtUsername.setText(otherUsername);
        txtPhone.setText(otherPhone);

        FirebaseUtil.getOtherUserAvatarReference(otherUserId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    AndroidUtil.setUriToImageView(ChatInfoActivity.this, uri, imvAvatar);

                }
            }
        });

        btnPinMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatInfoActivity.this, PinMessageActivity.class);
                intent.putExtra("chatroomId", chatroomId);
                startActivity(intent);
            }
        });

        btnMediaStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatInfoActivity.this, MediaStoreActivity.class);
                intent.putExtra("chatroomId", chatroomId);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}