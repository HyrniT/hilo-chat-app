package com.example.hilo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.hilo.adapter.MediaFileRecyclerAdapter;
import com.example.hilo.adapter.PinMessageRecyclerAdapter;
import com.example.hilo.model.ChatroomModel;
import com.example.hilo.model.MessageModel;
import com.example.hilo.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class MediaStoreActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private RecyclerView recyclerViewMedia;
    private MediaFileRecyclerAdapter adapter;
    private String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store);

        Intent intent = getIntent();
        if (intent != null) {
            chatroomId = intent.getStringExtra("chatroomId");
        }

        btnBack = findViewById(R.id.btnBack);
        recyclerViewMedia = findViewById(R.id.recyclerViewMedia);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setUpMediaRecyclerView();
    }

    private void setUpMediaRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageCollection(chatroomId)
                .orderBy("sentTimestamp", Query.Direction.DESCENDING)
                .whereNotEqualTo("imageUrl", null);

        FirestoreRecyclerOptions<MessageModel> options = new FirestoreRecyclerOptions.Builder<MessageModel>()
                .setQuery(query, MessageModel.class).build();

        if (adapter == null) {
            adapter = new MediaFileRecyclerAdapter(options, getApplicationContext());
            recyclerViewMedia.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerViewMedia.setAdapter(adapter);
            adapter.startListening();
        }
    }
}