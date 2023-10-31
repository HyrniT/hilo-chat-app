package com.example.hilo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hilo.model.UserModel;
import com.example.hilo.utils.AndroidUtil;

public class ChatActivity extends AppCompatActivity {

    private UserModel userModel;
    private EditText txtChat;
    private TextView txtUsername;
    private ImageButton btnSend, btnBack;
    private RecyclerView recyclerViewChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userModel = AndroidUtil.getUserModel(getIntent());

        txtChat = findViewById(R.id.txtChat);
        txtUsername = findViewById(R.id.txtUsername);
        btnSend = findViewById(R.id.btnSend);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        btnBack = findViewById(R.id.btnBack);

        txtUsername.setText(userModel.getUsername());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}