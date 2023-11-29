package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.hilo.adapter.MessageRecyclerAdapter;
import com.example.hilo.adapter.PinMessageRecyclerAdapter;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.AndroidUtil;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class PinMessageActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private RecyclerView recyclerViewPinMessage;
    private PinMessageRecyclerAdapter adapter;
    private String chatroomId;
    private ChatroomModel chatroomModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_message);

        Intent intent = getIntent();
        if (intent != null) {
            chatroomId = intent.getStringExtra("chatroomId");
            FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        chatroomModel = task.getResult().toObject(ChatroomModel.class);
                    }
                }
            });
        }

        btnBack = findViewById(R.id.btnBack);
        recyclerViewPinMessage = findViewById(R.id.recyclerViewPinMessage);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setUpMessageRecyclerView();
    }

    private void setUpMessageRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageCollection(chatroomId)
                .whereEqualTo("pinned", true)
                .orderBy("sentTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();

        if (adapter == null) {
            adapter = new PinMessageRecyclerAdapter(options, getApplicationContext(), chatroomModel);
            recyclerViewPinMessage.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewPinMessage.setAdapter(adapter);
            adapter.startListening();
        }
    }
}