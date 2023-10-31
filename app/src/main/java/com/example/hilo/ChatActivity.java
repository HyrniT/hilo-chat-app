package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    private UserModel otherUser;
    private ChatroomModel chatroomModel;
    private EditText txtChat;
    private TextView txtUsername;
    private ImageButton btnSend, btnBack;
    private RecyclerView recyclerViewChat;
    private String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModel(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.getCurrentUserId(), otherUser.getUserId());

        txtChat = findViewById(R.id.txtChat);
        txtUsername = findViewById(R.id.txtUsername);
        btnSend = findViewById(R.id.btnSend);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        btnBack = findViewById(R.id.btnBack);

        txtUsername.setText(otherUser.getUsername());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getChatroomModel();
    }

    private void getChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatroomModel = task.getResult().toObject(ChatroomModel.class);
                    if (chatroomModel == null) {
                        chatroomModel = new ChatroomModel(
                                chatroomId,
                                Arrays.asList(FirebaseUtil.getCurrentUserId(), otherUser.getUserId()),
                                Timestamp.now(),
                                ""
                        );
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                    }
                }
            }
        });
    }
}